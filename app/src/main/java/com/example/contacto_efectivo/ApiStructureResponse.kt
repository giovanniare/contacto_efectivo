package com.example.contacto_efectivo

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName


data class OperationApiResponse(
    val id: Int,
    val id_tipo_operacion: String,
    val codigo: String?,
    var status: String,
    val direccion_inicio: String?,
    val direccion_final: String?,
    val codigo_postal: Int?,
    val tarifa: Double?,
    val fecha_inicio: String,
    var fecha_final: String?,
    var cantidad: Int,
    var comentario: String?,
    val precio: Double?,
    val nombre_referencia: String?,
    val numero_referencia: String?,
    val repartidor: Int?,
    var historial: List<Movimiento>?,
    val peso: Int?,
    val largo: Int?,
    val ancho: Int?,
    val alto: Int?,
    var devoluciones: Int?,
    var entregas: Int?,
    //@SerializedName("inventario_relacion") val inventarioRelacion: String?,
    var imagen: String?,
    @SerializedName("imagen_opcional") var imagenOpcional: String?,
    @SerializedName("monicipio_id") val monicipioId: Int?,
    @SerializedName("municipio_nombre") val municipioNombre: String?,
    val finalizada: Boolean?,
    val pagado: Boolean?,
    @SerializedName("id_proveedor") val idProveedor: Int?
)

// Función para convertir el JSON a un objeto Operacion
fun parseJsonToOperacion(jsonString: String): OperationApiResponse? {
    return try {
        Gson().fromJson(jsonString, OperationApiResponse::class.java)
    } catch (e: Exception) {
        println("Error al parsear JSON: ${e.message}")
        null
    }
}
data class User(
    val id: Int,
    val id_tipo: Int,
    val nombre: String,
    val sueldo: Float?,
    val posicion: String,
    val fecha_inicio: String,
    val fecha_final: String?,
    val usuario_nombre: String,
    val usuario_password: String,
    val ganancia: Float?,
)

data class AuthData(
    val id: Int,
    val user_id: String,
    val token: String,
    val created: String
)

data class Movimiento(
    val fecha: Long,
    val status: String,
    val user: String?,
    val descripcion: String,
)

data class Municipio(
    val id : Int,
    val nombre : String,
)

data class MunicipiosResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<Municipio>
)

data class Proveedor(
    val id: Int,
    val nombre: String,
    val tarifa: Float?,
    val tarifa_contacto_efectivo: Float?,
    val tarifa_repartidor: Float?
)

data class ProveedoresResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<Proveedor>
)

data class Flujo(
    val creada: List<String>,
    val retorno: List<String>,
    @SerializedName("Asignada intento 1") var asignadaIntento1: List<String>,
    @SerializedName("Asignada intento 2") var asignadaIntento2: List<String>,
    @SerializedName("en ruta intento 1") var enRutaIntento1: List<String>,
    @SerializedName("ruta intento 2") var enRutaIntento2: List<String>,
    @SerializedName("intento 2") var intento2: List<String>,
) {
    operator fun get(key: String): List<String>? {
        return when (key) {
            "creada" -> creada
            "Asignada intento 1" -> asignadaIntento1
            "Asignada intento 2" -> asignadaIntento2
            "intento 2" -> intento2
            "en ruta intento 1" -> enRutaIntento1
            "ruta intento 2" -> enRutaIntento2
            "retorno" -> retorno
            else -> null
        }
    }
}

fun parseJsonToUser(jsonString: String): User? {
    return try {
        Gson().fromJson(jsonString, User::class.java)
    } catch (e: Exception) {
        println("Error al parsear JSON: ${e.message}")
        null
    }
}

