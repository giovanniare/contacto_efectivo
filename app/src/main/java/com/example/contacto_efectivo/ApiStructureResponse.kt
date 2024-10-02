package com.example.contacto_efectivo

import com.google.gson.Gson

data class OperationApiResponse(
    val url: String,
    val fecha_inicio: String,
    val id_tipo_operacion: String,
    val codigo: String?,
    val status: String,
    val direccion_inicio: String,
    val direccion_final: String,
    val tarifa: String,
    val fecha_final: String,
    val cantidad: Int,
    val comentario: String,
    val precio: String?,
    val nombre_referencia: String,
    val numero_referencia: String,
    val repartidor: String?,
    val historial: String?
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

data class FlowApiResponse(
    val creada: String,
    val agendada: String,
    val en_ruta: String,
    val efectiva: String,
    val transferencia: String,
    val cancelada: String,
    val entregada: String,
    val finalizada: String,
)

fun parseJsonToFlow(jsonString: String): FlowApiResponse? {
    return try {
        Gson().fromJson(jsonString, FlowApiResponse::class.java)
    } catch (e: Exception) {
        println("Error al parsear JSON: ${e.message}")
        null
    }
}