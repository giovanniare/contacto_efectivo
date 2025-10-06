package com.example.contacto_efectivo

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class OperationApiResponseDeserializer : JsonDeserializer<OperationApiResponse> {

    override fun deserialize(json: JsonElement, typeOfT: Type, ctx: JsonDeserializationContext): OperationApiResponse {
        val o = json.asJsonObject

        fun strOrNull(name: String): String? {
            val el = o.get(name) ?: return null
            if (el.isJsonNull) return null
            val v = el.asString
            return if (v.equals("null", ignoreCase = true)) null else v
        }

        fun intOrNull(name: String): Int? {
            val el = o.get(name) ?: return null
            return if (el.isJsonNull) null else el.asInt
        }

        fun doubleOrNull(name: String): Double? {
            val el = o.get(name) ?: return null
            return if (el.isJsonNull) null else el.asDouble
        }

        fun boolOrNull(name: String): Boolean? {
            val el = o.get(name) ?: return null
            return if (el.isJsonNull) null else el.asBoolean
        }

        val historialRaw = strOrNull("historial")
        val historial = parseHistorialString(historialRaw)

        return OperationApiResponse(
            id = intOrNull("id") ?: 0,
            id_tipo_operacion = strOrNull("id_tipo_operacion").orEmpty(),
            codigo = strOrNull("codigo"),
            status = strOrNull("status").orEmpty(),
            direccion_inicio = strOrNull("direccion_inicio"),
            direccion_final = strOrNull("direccion_final"),
            codigo_postal = intOrNull("codigo_postal"),
            tarifa = doubleOrNull("tarifa"),
            fecha_inicio = strOrNull("fecha_inicio").orEmpty(),
            fecha_final = strOrNull("fecha_final"),
            cantidad = intOrNull("cantidad") ?: 0,
            comentario = strOrNull("comentario"),
            precio = doubleOrNull("precio"),
            nombre_referencia = strOrNull("nombre_referencia"),
            numero_referencia = strOrNull("numero_referencia"),
            repartidor = intOrNull("repartidor"),
            historial = historial,
            peso = intOrNull("peso"),
            largo = intOrNull("largo"),
            ancho = intOrNull("ancho"),
            alto = intOrNull("alto"),
            devoluciones = intOrNull("devoluciones"),
            entregas = intOrNull("entregas"),
            imagen = strOrNull("imagen"),
            imagenOpcional = strOrNull("imagen_opcional"),
            monicipioId = intOrNull("monicipio_id"),
            municipioNombre = strOrNull("municipio_nombre"),
            finalizada = boolOrNull("finalizada"),
            pagado = boolOrNull("pagado"),
            idProveedor = intOrNull("id_proveedor")
        )
    }

    private fun parseHistorialString(raw: String?): List<Movimiento>? {
        if (raw.isNullOrBlank()) return null
        val trimmed = raw.trim()

        // Caso "OrderedDict()" vacío
        if (trimmed.equals("OrderedDict()", ignoreCase = true)) return emptyList()

        // Normalizamos: quitamos OrderedDict( y ) y cambiamos ' por "
        var s = trimmed
            .replace("OrderedDict(", "")
            .replace(")", "")
            .replace("'", "\"")

        return try {
            // Intenta como array: [{...}, {...}]
            val arr = JSONArray(s)
            (0 until arr.length()).map { i ->
                val obj = arr.getJSONObject(i)
                Movimiento(
                    fecha = obj.optLong("fecha"),
                    status = obj.optString("status"),
                    user = obj.optString("user"),
                    descripcion = obj.optString("descripcion")
                )
            }
        } catch (_: Exception) {
            // Intenta como objeto único: {...}
            try {
                val obj = JSONObject(s)
                listOf(
                    Movimiento(
                        fecha = obj.optLong("fecha"),
                        status = obj.optString("status"),
                        user = obj.optString("user"),
                        descripcion = obj.optString("descripcion")
                    )
                )
            } catch (_: Exception) {
                null
            }
        }
    }
}

class HttpRequests {
    private val urlApiBase_ = "https://walrus-app-ja4xp.ondigitalocean.app"
    private val client = OkHttpClient()
    private val gson: Gson = GsonBuilder()
        .serializeNulls()
        .registerTypeAdapter(OperationApiResponse::class.java, OperationApiResponseDeserializer())
        .setLenient()
        .create()

    suspend fun getOperation(endPointStr: String, token: String?): OperationApiResponse? {
        if (token == null || token == "") {
            println("Token invalido desde: **** getOperation ****")
            return null
        }

        println("Esta es la url que se manda: $urlApiBase_/$endPointStr")
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("$urlApiBase_/$endPointStr")
                .addHeader("token", token)
                .build()

            try {
                val response: Response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    responseBody?.let {
                        // Parsear el JSON a ApiResponse
                        return@withContext gson.fromJson(it, OperationApiResponse::class.java)
                    }
                } else {
                    println("Error: ${response.code}")
                    null
                }
            } catch (e: Exception) {
                println("Exception: ${e.message}")
                null
            }
        }
    }

    suspend fun getAllRepartidor(endPointStr: String, token: String?, repartidorId: String?): List<OperationApiResponse>? {
        if (token == null || token == "") {
            println("Token invalido desde: **** getAllRepartidor ****")
            return null
        }

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1) // Le sumas un día

        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = formatter.format(calendar.time)
        println("Formatted Date: $formattedDate")

        val jsonBody = """
            {
                "repartidor": $repartidorId,
                "fecha1": "2024-01-01",
                "fecha2": "$formattedDate",
                "finalizada": false
            }
        """

        val requestBody = jsonBody.toRequestBody("application/json; charset=utf-8".toMediaType())
        println("Post data: $jsonBody")
        println("Esta es la url que se manda: $urlApiBase_/$endPointStr")
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("$urlApiBase_/$endPointStr")
                .addHeader("token", token)
                .post(requestBody) // Método POST
                .build()

            try {
                val response: Response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    responseBody?.let {
                        val listType: Type = object : TypeToken<List<OperationApiResponse>>() {}.type
                        return@withContext gson.fromJson<List<OperationApiResponse>>(it, listType)
                    }
                } else {
                    println("Error: ${response.code}")
                    println("Error: ${response.body?.string()}")
                    null
                }
            } catch (e: Exception) {
                println("Exception: ${e.message}")
                null
            }
        }
    }

    suspend fun getUser(employeeId: String, token: String?): User? {
        if (token == null || token == "") {
            println("Token invalido desde: **** getUser ****")
            return null
        }

        println("Esta es la url que se manda: $urlApiBase_/empleados/$employeeId")
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("$urlApiBase_/empleados/$employeeId")
                .addHeader("token", token)
                .build()

            try {
                val response: Response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    val responseBody = body?.drop(1)?.dropLast(1)
                    println("Esta es la respuesta: $responseBody")
                    responseBody?.let {
                        // Parsear el JSON a ApiResponse
                        gson.fromJson(it, User::class.java)
                    }
                } else {
                    println("Error: ${response.code}")
                    null
                }
            } catch (e: Exception) {
                println("Exception: ${e.message}")
                println("Exception: ${e.localizedMessage}")
                println("Exception: ${e.stackTrace}")
                println("Exception: ${e.toString()}")
                null
            }
        }
    }

    suspend fun auth(user: String, pass: String): AuthData? {
        println("Esta es la url que se manda: $urlApiBase_/auth/log_in/")
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("$urlApiBase_/auth/log_in/")
                .addHeader("usuario", user)
                .addHeader("pass", pass)
                .build()

            try {
                val response: Response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    println("Esta es la respuesta: $responseBody")
                    responseBody?.let {
                        // Parsear el JSON a ApiResponse
                        gson.fromJson(it, AuthData::class.java)
                    }
                } else {
                    println("Error: ${response.code}")
                    null
                }
            } catch (e: Exception) {
                println("Exception: ${e.message}")
                println("Exception: ${e.localizedMessage}")
                println("Exception: ${e.stackTrace}")
                println("Exception: ${e.toString()}")
                null
            }
        }
    }
    suspend fun validateToken(token: String?): AuthData? {
        if (token == null || token == "") {
            return null
        }

        println("Esta es la url que se manda: $urlApiBase_/auth/verify/")
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("$urlApiBase_/auth/verify/")
                .addHeader("token", token)
                .build()

            try {
                val response: Response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    responseBody?.let {
                        // Parsear el JSON a ApiResponse
                        gson.fromJson(it, AuthData::class.java)
                    }
                } else {
                    println("Error: ${response.code}")
                    null
                }
            } catch (e: Exception) {
                println("Exception: ${e.message}")
                println("Exception: ${e.localizedMessage}")
                println("Exception: ${e.stackTrace}")
                println("Exception: ${e.toString()}")
                null
            }
        }
    }
    suspend fun logOut(token: String?): Boolean? {
        println("Logout Token: $token")
        if (token == null || token == "") {
            return null
        }

        val jsonBody = """
            {
                "token": "$token"
            }
        """

        val requestBody = jsonBody.toRequestBody("application/json; charset=utf-8".toMediaType())
        println("Esta es la url que se manda: $urlApiBase_/auth/log_out/")
        val request = Request.Builder()
            .url("$urlApiBase_/auth/log_out/")
            .addHeader("token", token)
            .post(requestBody) // Método POST
            .build()

        return withContext(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    println("Logout exitoso: ${response.body?.string()}")
                    true // Retorna el cuerpo de la respuesta
                } else {
                    println("Error: ${response.code}")
                    println("Error: ${response.message}")
                    println("Error: ${response.body?.string()}")
                    null
                }
            } catch (e: Exception) {
                println("Excepcion: ${e.message}")
                null
            }
        }
    }

    suspend fun getMunicipios(token: String?): List<Municipio>? {
        if (token == null || token == "") {
            return emptyList()
        }

        println("Esta es la url que se manda: $urlApiBase_/municipios/")
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("$urlApiBase_/municipios")
                .addHeader("token", token)
                .build()

            try {
                val response: Response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    responseBody?.let {
                        val municipiosResponse = gson.fromJson(it, MunicipiosResponse::class.java)
                        municipiosResponse.results
                    }
                } else {
                    println("Error: ${response.code}")
                    println("Error: ${response.body?.string()}")
                    emptyList()
                }
            } catch (e: Exception) {
                println("Exception: ${e.message}")
                emptyList()
            }
        }

    }

    suspend fun getProveedores(token: String?): List<Proveedor>? {
        if (token == null || token == "") {
            return emptyList()
        }

        println("Esta es la url que se manda: $urlApiBase_/proveedores/")
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("$urlApiBase_/proveedores")
                .addHeader("token", token)
                .build()

            try {
                val response: Response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    responseBody?.let {
                        val proveedoresResponse = gson.fromJson(it, ProveedoresResponse::class.java)
                        proveedoresResponse.results
                    }
                } else {
                    println("Error: ${response.code}")
                    println("Error: ${response.body?.string()}")
                    emptyList()
                }
            } catch (e: Exception) {
                println("Exception: ${e.message}")
                emptyList()
            }
        }
    }

    suspend fun getFlujo(): Flujo? {

        println("Esta es la url que se manda: $urlApiBase_/flujo_operacion_2/")
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("$urlApiBase_/flujo_operacion_2")
                .build()

            try {
                val response: Response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    responseBody?.let {
                        // Parsear el JSON a ApiResponse
                        gson.fromJson(it, Flujo::class.java)
                    }
                } else {
                    println("Error: ${response.code}")
                    null
                }
            } catch (e: Exception) {
                println("Exception: ${e.message}")
                println("Exception: ${e.localizedMessage}")
                println("Exception: ${e.stackTrace}")
                println("Exception: ${e.toString()}")
                null
            }
        }
    }
}
