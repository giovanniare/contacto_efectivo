package com.example.contacto_efectivo

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun UpdateScreen(navController: NavController, viewModel: OperationsViewModel) {

    BackHandler {
        viewModel.tipoOperacion.value = null
        viewModel.imageName.value = null
        viewModel.imageCompressed.value = null
        viewModel.imageUri.value = null
        viewModel.getData.value = true
        navController.navigate("home_screen")
    }

    var operationStatus = remember { mutableStateOf("") }
    val opIdDialog = remember { mutableStateOf(true) }
    val success = remember { mutableStateOf(false) }
    val operationData = remember { mutableStateOf<OperationApiResponse?>(null) }
    var canUpdateStatus = remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val tokenManager = TokenManager(LocalContext.current)
    val repartidorId = tokenManager.getUserId()?.toInt()

    viewModel.onBackfromScanScreen.value = "update_screen"

    if (viewModel.keepData.value) {
        opIdDialog.value = false
    }

    if (viewModel.dataFromSelectedItem.value != null && viewModel.operationSelected != null) {
        success.value = true
        operationData.value = viewModel.operationSelected
        println("Data scanned: ${operationData.value}")
        opIdDialog.value = false
    } else if (opIdDialog.value && !viewModel.keepData.value) {
        AskOperationId(
            operationDialog = opIdDialog,
            success = success,
            data = operationData,
            onScanScreen = {navController.navigate("scan_screen")},
            navController = navController,
            viewModel = viewModel
        )
    }

    var parsedData = operationData.value
    if (parsedData != null) {
        operationStatus.value = parsedData.status
    } else if (viewModel.keepData.value && viewModel.operationScaneed != null) {
        parsedData = viewModel.operationScaneed
        operationStatus.value = parsedData?.status ?: "Sin estado"
    } else {
        // Manejar el caso en el que parsedData es nulo, por ejemplo, asignar un valor predeterminado a selectedItem
        operationStatus.value = "Sin estado"  // o el valor que sea adecuado para tu lógica
    }

    if (operationStatus.value !in viewModel.noMoreActions && parsedData != null) {
        canUpdateStatus.value = true
        // println("SE puede actualizar: ${operationStatus.value}")
        // println("Data: ${parsedData}")
    }

    LaunchedEffect(operationStatus.value) {
        // Forzar la recomposición cuando cambia operationStatus
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Banner(navController, viewModel)
        TitleText(title = "Actualizacion de estatus")
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            if (parsedData != null) {
                if (parsedData.id_tipo_operacion != viewModel.tipoOperacion.value) {
                    Text(
                        text = "No puedes actualizar esta operacion desde este menu. Selecciona ${parsedData.id_tipo_operacion} en el menu de operaciones para continuar.",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF213E85)
                    )
                } else if (!canUpdateStatus.value) {
                    Text(
                        text = "Lo siento, no puedes actualizar el estatus de esta operacion. Comunicate con tu analista. Esta operacion ya fue marcada como entregada.",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF213E85)
                    )
                } else if(repartidorId != parsedData.repartidor) {
                    Text(
                        text = "Esta operacion esta asignada a otro repartidor. Comunicate con tu analizta.",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF213E85)
                    )
                }else {
                    Options(
                        navController = navController,
                        viewModel = viewModel,
                        operationStatus = operationStatus,
                        canUpdateStatus = canUpdateStatus,
                        operationData = parsedData
                    )
                }
            }

        }
    }

    LaunchedEffect(operationData.value) {
        viewModel.operationSelected = null
        viewModel.dataFromSelectedItem.value = null
    }
}

@Composable
fun Options(
    navController: NavController,
    viewModel: OperationsViewModel,
    operationStatus: MutableState<String>,
    canUpdateStatus: MutableState<Boolean>,
    operationData: OperationApiResponse?
) {
    var expanded by remember { mutableStateOf(false) }
    var comments by remember { mutableStateOf("") }
    var nextOptions by remember { mutableStateOf(listOf("")) }
    var nextStatus = remember { mutableStateOf("") }
    val opIdDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (nextStatus.value == "" && operationStatus.value != "Sin estado") {
        nextStatus.value = operationStatus.value
    }

    if (viewModel.keepData.value) {
        nextStatus.value = viewModel.nextStatus.value!!
    }

    // Actualiza el valor de las opciones basado en el estado de operationStatus
    when (operationStatus.value) {
        "creada" -> nextOptions = viewModel.agendada
        "agendada" -> nextOptions = viewModel.agendada
        "asignada" -> nextOptions = viewModel.agendada
        "en ruta" -> nextOptions = viewModel.enRuta
        //"efectiva" -> nextOptions = viewModel.efectiva
        "transferencia" -> nextOptions = viewModel.transferecia
        else -> {
            canUpdateStatus.value = false
            nextOptions = viewModel.noMoreActions
        }
    }

    Text(
        text = "${operationData?.id_tipo_operacion} : ${operationData?.codigo}",
        fontSize = 25.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF213E85)
    )
    if (operationData != null) {
        CallButton(phoneNumber = operationData.numero_referencia)
    }

    Text(
        text = "Selecciona el nuevo estatus de la operacion",
        fontSize = 17.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF213E85)
    )

    // Aquí está el componente Box que contiene el botón y el menú desplegable
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 7.dp, bottom = 7.dp)
    ) {
        Button(
            onClick = { expanded = !expanded },
            shape = RoundedCornerShape(13.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    BorderStroke(1.dp, Color(0xFFE8D67E)),
                    shape = RoundedCornerShape(13.dp)
                )
        ) {
            // Usamos una clave de recomposición para forzar que el texto se actualice
            Text(
                text = nextStatus.value,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.inter_extrabold)),
                color = Color(0xFF213E85)
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,  // Reemplaza con el ícono adecuado
                contentDescription = "Entrega a domicilio",
                tint = Color(0xFF213E85)
            )
        }

        // Dropdown menu con las opciones
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            nextOptions.forEach { estatus ->
                DropdownMenuItem(
                    text = { Text(text = estatus) },
                    onClick = {
                        nextStatus.value = estatus  // Aquí actualizamos el estado
                        expanded = false
                    }
                )
            }
        }
    }

    // Verifica si operationStatus.value está en la lista de estados que necesitan evidencia
    if (nextStatus.value in viewModel.necesitaEvidencia) {
        Row(modifier = Modifier.padding(top = 16.dp)){
            Text(
                text = "Adjunta evidencia",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.blue_btn),
                modifier = Modifier
                    .weight(2f)
                    .align(Alignment.CenterVertically)
            )
            Button(
                onClick = { opIdDialog.value = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Filled.AddCircle,
                    contentDescription = "Tomar foto",
                    tint = Color(0xFF213E85)
                )
            }
        }
        if (viewModel.imageName.value != null && viewModel.imageCompressed.value != null) {
            Text(
                text = "${viewModel.imageName.value}",
                fontSize = 7.sp,
                color = Color(0xFF213E85),
                modifier = Modifier
                    .padding(top = 0.3.dp)
            )
        }
        Text(
            text = "Comentarios Adicionales",
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF213E85),
            modifier = Modifier
                .padding(top = 36.dp)
        )
        TextField(
            value = comments,
            onValueChange = { newText ->
                comments = newText
                if (operationData != null) {
                    operationData.comentario = newText
                }},
            label = {
                Text(
                    text = "Escribe aqui cualquier incidencia",
                    color = Color(0xFF888888),
                    fontSize = 14.sp
                )
            },
            maxLines = Int.MAX_VALUE,
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.Transparent,
                unfocusedContainerColor = Color(0xFFe1e6ed)
            ),
            shape = RoundedCornerShape(13.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .padding(top = 7.dp, bottom = 7.dp)
        )

        if (opIdDialog.value) {
            TomarEvidencia(
                operationDialog = opIdDialog,
                viewModel,
                onPhotoScreen = {
                    if (operationData != null) {
                        viewModel.keepData.value = true
                        viewModel.nextStatus.value = nextStatus.value
                        viewModel.operationScaneed = operationData
                        navController.navigate("photo_screen") }
                    }
            )
        }

    }

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(top = 23.dp, start = 28.dp, end = 28.dp)
            .fillMaxWidth()
    ) {
        Button(
            onClick = {
                operationData?.imagen = viewModel.imageCompressed.value
                println("Imagen bytearray: ${viewModel.imageCompressed.value}")
                viewModel.imageName.value = null
                viewModel.imageCompressed.value = null
                viewModel.imageUri.value = null
                if (operationData != null) {
                    operationData.status = nextStatus.value
                    var codigo= operationData.codigo.toString()
                    viewModel.operationSelected = null
                    viewModel.getData.value = true
                    sendUpdate({
                        navController.navigate("home_screen") {
                            launchSingleTop = true // Evita duplicados en la pila
                            restoreState = false   // Ignora el estado guardado
                            popUpTo("home_screen") { inclusive = true }
                        }

                    }, operationData, codigo, context)
                } else {
                    //
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(13.dp),
            border = BorderStroke(1.dp, Color(0xFFE8D67E)),
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .padding(end = 8.dp)
        ) {
            Text(
                text = "Mover",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE8D67E)
            )
        }
        Button(
            onClick = {
                viewModel.imageName.value = null
                viewModel.imageCompressed.value = null
                viewModel.imageUri.value = null
                navController.navigate("home_screen") {
                    launchSingleTop = true // Evita duplicados en la pila
                    restoreState = false   // Ignora el estado guardado
                    popUpTo("home_screen") { inclusive = true }
                } },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF213E85)),
            shape = RoundedCornerShape(13.dp),
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .padding(start = 8.dp)
        ) {
            Text(
                text = "Cancelar",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}


private fun sendUpdate(
    onNavigateToHome: () -> Unit,
    operationData: OperationApiResponse?,
    codigo: String,
    context: Context
) {
    if (operationData != null) {

        val viewModel = OperationsViewModel()
        var canUpdate = false

        if (viewModel.necesitaEvidencia.contains(operationData.status) && operationData.imagen != null) {
            canUpdate = true
        } else if (!viewModel.necesitaEvidencia.contains(operationData.status)) {
            canUpdate = true
        } else {
            canUpdate = false
        }

        if (canUpdate) {
            val tokenManager = TokenManager(context)
            updateOperationStatus(codigo, operationData, tokenManager.getToken())
            viewModel.getData.value = true

            onNavigateToHome()

        } else {
            Toast.makeText(context, "Sube una imagen de evidencia para continuar", Toast.LENGTH_LONG).show()
        }

    }
}
