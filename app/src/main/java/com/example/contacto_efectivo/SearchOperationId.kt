package com.example.contacto_efectivo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.gson.Gson

@Composable
fun AskOperationId(
    operationDialog: MutableState<Boolean>,
    success: MutableState<Boolean>,
    data: MutableState<String?>,
    onScanScreen: () -> Unit,
    viewModel: OperationsViewModel,
    navController: NavController
)
{
    val userInput = remember { mutableStateOf("") }
    val httpRequests = HttpRequests()
    var loading by remember { mutableStateOf(false) }

    // Observa el valor de scannedCode en el ViewModel
    LaunchedEffect(viewModel.operationId.value) {
        // Verifica si hay un código escaneado
        viewModel.operationId.value?.let { scannedCode ->
            userInput.value = scannedCode // Actualiza el input con el código escaneado
            viewModel.operationId.value = null // Reinicia el código escaneado
        }
    }

    if (operationDialog.value) {
        AlertDialog(
            onDismissRequest = { operationDialog.value = false},
            title = { Text(text = "ID de operacion") },
            text = {
                Column {
                    Text("Presiona para escanea el ID")
                    Button(
                        onClick = {
                            viewModel.operationId.value = null
                            onScanScreen() },
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Text(text = "Escanear")
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Escanear",
                            tint = Color(0xFF213E85)
                        )
                    }
                    Text("Introduce el ID")
                    TextField(
                        value = userInput.value,
                        onValueChange = { newText -> userInput.value = newText },
                        label = { Text("ID") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = { loading = true })
                {
                    Text("Buscar")
                }
            },
            dismissButton = {
                Button(onClick = { operationDialog.value = false }) {
                    Text("Cancelar")
                }
            }
        )

        // Usar LaunchedEffect fuera del AlertDialog
        if (loading) {
            LaunchedEffect(Unit) {
                val apiResponse = httpRequests.getOperation("operacion/${userInput.value}/codigo/")
                loading = false
                apiResponse?.let {
                    // Convierte el objeto a JSON para la navegación
                    data.value = Gson().toJson(it)
                    success.value = true
                }
                operationDialog.value = false
            }
        }

        println("Este es el valor escaneado - viewModel: ${viewModel.operationId.value}")
        // Verifica si hay un código escaneado
        if (!viewModel.operationId.value.isNullOrEmpty()) {
            userInput.value = viewModel.operationId.value!! // Actualiza el input con el código escaneado
            println("Este es el valor escaneado - User Value: ${userInput.value}")
            println("Este es el valor escaneado - viewModel: ${viewModel.operationId.value}")

            LaunchedEffect(Unit) {
                val apiResponse = httpRequests.getOperation("operacion/${userInput.value}/codigo/")
                apiResponse?.let {
                    // Convierte el objeto a JSON para la navegación
                    data.value = Gson().toJson(it)
                    success.value = true
                }
                operationDialog.value = false
                //viewModel.operationId.value = null // Reinicia el código escaneado
            }
        }
    }
}
