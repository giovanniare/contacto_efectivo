package com.example.contacto_efectivo

import com.google.gson.Gson

data class OperationApiResponse(
    val id: Int,
    val id_tipo_operacion: String,
    val fecha_inicio: String,
    val codigo: String?,
    var status: String,
    val direccion_inicio: String,
    val direccion_final: String,
    val tarifa: String,
    val fecha_final: String,
    var cantidad: Int,
    var comentario: String,
    val precio: String?,
    val nombre_referencia: String,
    val numero_referencia: String,
    val repartidor: Int?,
    var historial: String?,
    val peso: Int,
    val largo: Int,
    val ancho: Int,
    val alto: Int,
    var entregas: Int,
    var devoluciones: Int
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
    val sueldo: String,
    val posicion: String,
    val fecha_inicio: String,
    val fecha_final: String,
    val usuario_nombre: String,
    val usuario_password: String
)

fun parseJsonToUser(jsonString: String): User? {
    return try {
        Gson().fromJson(jsonString, User::class.java)
    } catch (e: Exception) {
        println("Error al parsear JSON: ${e.message}")
        null
    }
}
