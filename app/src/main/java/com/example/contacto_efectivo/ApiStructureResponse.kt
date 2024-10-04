package com.example.contacto_efectivo

import com.google.gson.Gson

data class OperationApiResponse(
    val url: String,
    val fecha_inicio: String,
    val id_tipo_operacion: String,
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