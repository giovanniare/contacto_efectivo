package com.example.contacto_efectivo

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class HttpRequests {
    private val urlApiBase_ = "https://walrus-app-ja4xp.ondigitalocean.app"
    private val client = OkHttpClient()
    private val gson = Gson() // Inicializa Gson

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
                        gson.fromJson(it, OperationApiResponse::class.java)
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
                        // Parsear el JSON a una lista de ApiResponse
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
}
