package com.example.contacto_efectivo

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.activity.compose.rememberLauncherForActivityResult as rememberLauncherForActivityResult1

@SuppressLint("ResourceAsColor")
@Composable
private fun StartRoute(onNavigateToHome: () -> Unit, onNavigateToPhoto: () -> Unit, viewModel: OperationsViewModel) {
    var count by remember { mutableIntStateOf(0) }
    val opIdDialog = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 7.dp, end = 7.dp, top = 29.dp)
    ) {
        Row(modifier = Modifier.padding(bottom = 16.dp)){
            Text(
                text = "Paquetes Recibidos",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.blue_btn),
                modifier = Modifier
                    .weight(2f)
                    .align(Alignment.CenterVertically)
            )
            TextField(
                value = count.toString(),
                onValueChange = {newValue ->  count = newValue.toIntOrNull() ?: 0 },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(13.dp),
                placeholder = { Text(
                    text = count.toString(),
                    color = colorResource(id = R.color.gray_ratiio),
                    fontSize = 14.sp)
                },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFe1e6ed),
                    unfocusedTextColor = colorResource(id = R.color.blue_btn)
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)

            )
        }
        Row(modifier = Modifier.padding(bottom = 16.dp, top = 16.dp)){
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
        if (count > 0) {
            Button(
                onClick = {
                    viewModel.thirdOperationInCourse.value = true
                    sendUpdate(viewModel, recibidos = count)
                    onNavigateToHome() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF213E85)),
                shape = RoundedCornerShape(13.dp),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .height(65.dp)
                    .padding(top = 16.dp)
            ) {
                Text(
                    text = "Iniciar Ruta",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        } else {
            Text(
                text = "Llena el campo de recibidos para poder actualiazar el estatus.",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.blue_btn),
                modifier = Modifier
                    .weight(2f)
                    .align(Alignment.CenterHorizontally)
            )
        }

    }

    if (opIdDialog.value) {
        TomarEvidencia(
            operationDialog = opIdDialog,
            onNavigateToGallery = { /* Aquí va la lógica al presionar el botón */ },
            onPhotoScreen = {
                viewModel.keepData.value = true
                onNavigateToPhoto() }
        )
    }
}

@Composable
private fun EndRoute(onNavigateToHome: () -> Unit, onNavigateToPhoto: () -> Unit, viewModel: OperationsViewModel) {
    var entregados by remember { mutableIntStateOf(0) }
    var devoluciones by remember { mutableIntStateOf(0) }
    val opIdDialog = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 7.dp, end = 7.dp, top = 29.dp)
    ) {
        Row(modifier = Modifier.padding(bottom = 16.dp)){
            Text(
                text = "Paquetes Entregados",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.blue_btn),
                modifier = Modifier
                    .weight(2f)
                    .align(Alignment.CenterVertically)
            )
            TextField(
                value = entregados.toString(),
                onValueChange = { newValue -> entregados = newValue.toIntOrNull() ?: 0 },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(13.dp),
                placeholder = { Text(
                    text = entregados.toString(),
                    color = colorResource(id = R.color.gray_ratiio),
                    fontSize = 14.sp)
                },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFe1e6ed),
                    unfocusedTextColor = colorResource(id = R.color.blue_btn)
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)

            )
        }
        Row(modifier = Modifier.padding(bottom = 16.dp)) {
            Text(
                text = "Devoluciones",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.blue_btn),
                modifier = Modifier
                    .weight(2f)
                    .align(Alignment.CenterVertically)
            )
            TextField(
                value = devoluciones.toString(),
                onValueChange = { newValue -> devoluciones = newValue.toIntOrNull() ?: 0 },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(13.dp),
                placeholder = { Text(
                    text = devoluciones.toString(),
                    color = colorResource(id = R.color.gray_ratiio),
                    fontSize = 14.sp)
                },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFe1e6ed),
                    unfocusedTextColor = colorResource(id = R.color.blue_btn)
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)

            )
        }
        Row(modifier = Modifier.padding(bottom = 16.dp, top = 16.dp)){
            Text(
                text = "Adjunta evidencia",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.blue_btn),
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
        if (entregados > 0 && devoluciones >= 0) {
            Button(
                onClick = {
                    viewModel.thirdOperationInCourse.value = false
                    sendUpdate(viewModel, end = true, entregados = entregados, devoluciones = devoluciones)
                    entregados = 0
                    devoluciones = 0
                    onNavigateToHome() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF213E85)),
                shape = RoundedCornerShape(13.dp),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .height(65.dp)
                    .padding(top = 16.dp)
            ) {
                Text(
                    text = "Finalizar Ruta",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        } else {
            Text(
                text = "Llena los campos de entragas y devoluciones para poder actualiazar el estatus.",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.blue_btn),
                modifier = Modifier
                    .weight(2f)
                    .align(Alignment.CenterHorizontally)
            )
        }

    }

    if (opIdDialog.value) {
        TomarEvidencia(
            operationDialog = opIdDialog,
            onNavigateToGallery = { /* Aquí va la lógica al presionar el botón */ },
            onPhotoScreen = {
                viewModel.keepData.value = true
                onNavigateToPhoto() }
        )
    }
}

@Composable
fun ThirdScreen(navController: NavController, viewModel: OperationsViewModel) {
    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf("Iniciar/Finalizar") }

    selectedItem = if (viewModel.thirdOperationInCourse.value == true) {
        "Finalizar ruta"
    } else {
        "Iniciar ruta"
    }


    BackHandler {
        viewModel.tipoOperacion.value = null
        navController.navigate("home_screen")
    }

    var operationStatus = remember { mutableStateOf("") }
    val opIdDialog = remember { mutableStateOf(true) }
    val success = remember { mutableStateOf(false) }
    val operationData = remember { mutableStateOf<OperationApiResponse?>(null) }
    var canUpdateStatus = remember { mutableStateOf(false) }

    viewModel.onBackfromScanScreen.value = "third_screen"

    if (viewModel.keepData.value) {
        opIdDialog.value = false
    }

    if (opIdDialog.value && !viewModel.keepData.value) {
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
        viewModel.operationScaneed = parsedData
    } else if (viewModel.keepData.value && viewModel.operationScaneed != null) {
        parsedData = viewModel.operationScaneed
        operationStatus.value = parsedData?.status ?: "Sin estado"
    } else {
        // Manejar el caso en el que parsedData es nulo, por ejemplo, asignar un valor predeterminado a selectedItem
        operationStatus.value = "Sin estado"  // o el valor que sea adecuado para tu lógica
    }

    if (operationStatus.value !in viewModel.noMoreActions && parsedData != null) {
        canUpdateStatus.value = true
        println("SE puede actualizar: ${operationStatus.value}")
        println("Data: ${parsedData}")
    }

    LaunchedEffect(operationStatus.value) {
        // Forzar la recomposición cuando cambia operationStatus
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Banner()
            TitleText(title = "Operaciones Terceros")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                if (parsedData != null) {
                    if (parsedData.id_tipo_operacion != viewModel.tipoOperacion.value) {
                        selectedItem = "No permitido"
                        Text(
                            text = "No puedes actualizar esta operacion desde este menu. Selecciona ${parsedData.id_tipo_operacion} en el menu de operaciones para continuar.",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF213E85)
                        )
                    } else if (!canUpdateStatus.value) {
                        selectedItem = "No permitido"
                        Text(
                            text = "Lo siento, no puedes actualizar el estatus de esta operacion. Comunicate con tu analista.",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF213E85)
                        )
                    } else if(viewModel.repartidorId.value != parsedData.repartidor) {
                        selectedItem = "No repartidor match"
                    }else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 7.dp, end = 7.dp)
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
                                Text(
                                    text = selectedItem,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF213E85)
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown, // Reemplaza con el icono adecuado
                                    contentDescription = "Entrega a domicilio",
                                    tint = Color(0xFF213E85)
                                )
                            }
                            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                DropdownMenuItem(
                                    text = {
                                        Text(text = "Iniciar ruta")
                                    },
                                    onClick = {
                                        selectedItem = "Iniciar ruta"
                                        expanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text(text = "Finalizar ruta")
                                    },
                                    onClick = {
                                        selectedItem = "Finalizar ruta"
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                } else {
                    Text(
                        text = "No tienes acceso a esta operacion",
                        fontSize = 30.sp,
                        color = Color(0xFF213E85),
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily(Font(R.font.inter_extrabold)),
                        modifier = Modifier
                            .padding(top = 30.dp)
                            .align(Alignment.CenterHorizontally)

                    )
                }
                when (selectedItem) {
                    "Iniciar ruta" -> {
                        if (parsedData != null) {
                            StartRoute({ navController.navigate("home_screen") }, {
                                viewModel.keepData.value = true
                                navController.navigate("photo_screen")
                            }, viewModel)
                        }
                    }
                    "Finalizar ruta" -> {
                        if (parsedData != null) {
                            EndRoute({ navController.navigate("home_screen") }, {
                                viewModel.keepData.value = true
                                navController.navigate("photo_screen")
                            }, viewModel)
                        }
                    }
                    "No permitido" -> Text(
                        text = "No puedes actualizar el estatus de esta operacion",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF213E85)
                    )
                    "No repartidor match" -> Text(
                        text = "Esta operacion esta asignada a otro repartidor. Comunicate con tu analizta.",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF213E85)
                    )
                    else -> {
                        Text(
                            text = "Ocurrio un error. Reportalo a tu analizta",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF213E85))
                        viewModel.keepData.value = false
                        viewModel.operationScaneed = null
                    }
                }
            }
        }
    }
}

private fun sendUpdate(viewModel: OperationsViewModel, recibidos: Int = 0, entregados: Int = 0, devoluciones: Int = 0, end: Boolean = false, ) {
    var opData = viewModel.operationScaneed
    var codigo = opData?.codigo

    if (opData != null && codigo != null) {
        if (end) {
            opData.entregas = entregados
            opData.devoluciones = devoluciones
            opData.status = "entregada"
        } else {
            opData.cantidad = recibidos
            opData.status = "en ruta"
        }

        updateOperationStatus(codigo, opData)
        viewModel.keepData.value = false
        viewModel.operationScaneed = null
    }
}