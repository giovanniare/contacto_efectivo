package com.example.contacto_efectivo

import android.net.Uri
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
    var dataFromSelectedItem = mutableStateOf<Boolean?>(null)
    var operationSelected:OperationApiResponse? = null
    var imageName = mutableStateOf<String?>(null)
    var imageCompressed = mutableStateOf<String?>(null)
    var imageUri = mutableStateOf<Uri?>(null)
    var recibidos = mutableStateOf<String>("")
    var entregados = mutableStateOf<String>("")
    var devoluciones = mutableStateOf<String>("")
    var getData = mutableStateOf<Boolean>(true)
    var operationsList = mutableStateOf<List<OperationApiResponse>>(emptyList())
    var municipios = mutableStateOf<List<Municipio>>(emptyList())
    var proveedores = mutableStateOf<List<Proveedor>>(emptyList())

    val agendada = listOf("en ruta", "cancelada", "efectiva")
    val enRuta = listOf("efectiva", "transferencia", "reagendada", "cancelada")
    val efectiva = listOf("reagendada", "cancelada")
    val transferecia = listOf("reagendada", "cancelada", "efectiva")
    val noMoreActions = listOf("efectiva", "cancelada", "finalizada", "reagendada")
    val necesitaEvidencia = listOf("efectiva", "cancelada", "reagendada")
}