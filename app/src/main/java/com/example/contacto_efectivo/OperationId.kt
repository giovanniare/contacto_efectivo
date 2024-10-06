package com.example.contacto_efectivo

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class OperationsViewModel: ViewModel() {
    var operationId = mutableStateOf<String?>(null)
    var operationIdUrl = mutableStateOf<String?>(null)
    var thirdOperationInCourse = mutableStateOf<Boolean?>(null)
    var onBackfromScanScreen = mutableStateOf<String>("home_screen")
    var keepData = mutableStateOf(false)
    var operationScaneed:OperationApiResponse? = null
    var nextStatus = mutableStateOf<String?>(null)
    var repartidorId = mutableStateOf<Int?>(null)
    var tipoOperacion = mutableStateOf<String?>(null)

    val agendada = listOf("en ruta", "cancelada", "entregada")
    val enRuta = listOf("efectiva", "transferencia", "reagendada", "cancelada", "entregada")
    val efectiva = listOf("reagendada", "cancelada")
    val transferecia = listOf("reagendada", "cancelada", "entregada")
    val noMoreActions = listOf("entregada", "cancelada", "finalizada", "reagendada")
    val necesitaEvidencia = listOf("entregada", "cancelada", "reagendada")
}