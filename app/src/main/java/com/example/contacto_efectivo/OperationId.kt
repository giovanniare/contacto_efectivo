package com.example.contacto_efectivo

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OperationsViewModel: ViewModel() {
    val apiClient = HttpRequests()
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

    val agendada = listOf("en ruta", "cancelada", "intento 2")
    val enRuta = listOf("efectiva", "transferencia", "reagendada", "cancelada")
    val efectiva = listOf("reagendada", "cancelada")
    val transferecia = listOf("reagendada", "cancelada", "efectiva")
    val noMoreActions = listOf("efectiva", "cancelada", "finalizada", "reagendada", "intento 2", "retorno")
    val necesitaEvidencia = listOf("efectiva", "cancelada", "reagendada", "intento 2", "retorno")
    var flujoOperaciones: Flujo? = null

    var statusMessage = mutableStateOf<String>("")
    var nextStatusScreen = mutableStateOf<String>("home_screen")
    private val _operationScanned = MutableStateFlow<OperationApiResponse?>(null)
    val optScannedFromMultiScan: StateFlow<OperationApiResponse?> = _operationScanned

    fun getOperation(id:String, token:String?){
        viewModelScope.launch {
            try {
                val response = apiClient.getOperation("operacion/$id/codigo/", token)
                _operationScanned.value = response
                println("Response desde multiscan: $response")
            } catch (e: Exception) {
                println("Error al obtener la operación desde multiscan: $e")
                _operationScanned.value = null

            }
        }
    }

    fun clearOperation() {
        _operationScanned.value = null
    }

    fun enRutaOperacion(token:String?, userName:String?) {
        viewModelScope.launch {
            try {
                val operacion = operationScaneed
                if (operacion === null) return@launch

                var historial = operacion.historial
                val nuevoMovimiento = Movimiento(
                    fecha = System.currentTimeMillis(),
                    status = "en ruta",
                    user = userName,
                    descripcion = "Operacion actualizada por un repartidor desde el app movil"
                )

                historial = historial?.plus(nuevoMovimiento)
                operacion.historial = historial
                operacion.status = "en ruta"

                updateOperationStatus(operacion.id.toString(), operacion, token)
            } catch (e: Exception) {
                println("Error al actualizar la operación desde multiscan: $e")
                operationScaneed = null
            }

        }
    }

    fun getFlujoOperacion() {
        viewModelScope.launch {
            try {
                val response = apiClient.getFlujo()
                flujoOperaciones = response
                println("Flujo de operaciones: $flujoOperaciones")
            } catch (e: Exception) {
                println("No se pudo obtener el flujo de las operaciones")
                flujoOperaciones = null
            }

        }
    }
}