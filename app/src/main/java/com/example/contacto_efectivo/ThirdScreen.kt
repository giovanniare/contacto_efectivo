package com.example.contacto_efectivo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.activity.compose.rememberLauncherForActivityResult as rememberLauncherForActivityResult1

@SuppressLint("ResourceAsColor")
@Composable
private fun StartRoute(onNavigateToHome: () -> Unit, onNavigateToPhoto: () -> Unit, viewModel: OperationsViewModel) {
    var count = viewModel.recibidos
    val opIdDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val tokenManager = TokenManager(context)
    val token = tokenManager.getToken()
    val keyboardController = LocalSoftwareKeyboardController.current

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
                value = count.value,
                onValueChange = {newValue ->  count.value = newValue },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                    }
                ),
                shape = RoundedCornerShape(13.dp),
                placeholder = { Text(
                    text = count.value,
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
        if (count.value != "" && count.value != "0") {
            Button(
                onClick = {
                    if (viewModel.imageName.value != null) {
                        viewModel.thirdOperationInCourse.value = true
                        sendUpdate(viewModel, recibidos = count.value, token = token, context = context)
                        viewModel.getData.value = true
                        viewModel.operationSelected = null
                        viewModel.dataFromSelectedItem.value = null
                        viewModel.keepData.value = false
                        viewModel.operationScaneed = null
                        viewModel.dataFromSelectedItem.value = null

                        onNavigateToHome()
                    } else {
                        Toast.makeText(context, "Sube una imagen de evidencia para continuar", Toast.LENGTH_LONG).show()
                    }},
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
            viewModel,
            onPhotoScreen = {
                viewModel.keepData.value = true
                onNavigateToPhoto() }
        )
    }
}

@Composable
private fun EndRoute(onNavigateToHome: () -> Unit, onNavigateToPhoto: () -> Unit, viewModel: OperationsViewModel) {
    var entregados = viewModel.entregados
    var devoluciones = viewModel.devoluciones
    val opIdDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val tokenManager = TokenManager(context)
    val token = tokenManager.getToken()
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 7.dp, end = 7.dp, top = 29.dp)
    ) {
        Row(modifier = Modifier.padding(bottom = 16.dp)){
            Text(
                text = "Paquetes recibidos: ${viewModel.operationScaneed?.cantidad}",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.blue_btn),
                modifier = Modifier
                    .weight(2f)
                    .align(Alignment.CenterVertically)
            )
        }
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
                value = entregados.value,
                onValueChange = { newValue -> entregados.value = newValue },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                    }
                ),
                shape = RoundedCornerShape(13.dp),
                placeholder = { Text(
                    text = entregados.value,
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
                value = devoluciones.value,
                onValueChange = { newValue -> devoluciones.value = newValue },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                    }
                ),
                shape = RoundedCornerShape(13.dp),
                placeholder = { Text(
                    text = devoluciones.value,
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
        Row(modifier = Modifier.padding(top = 16.dp)){
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
        if (viewModel.imageName.value != null && viewModel.imageCompressed.value != null) {
            Text(
                text = "${viewModel.imageName.value}",
                fontSize = 7.sp,
                color = Color(0xFF213E85),
                modifier = Modifier
                    .padding(top = 0.3.dp)
            )
        }
        if (entregados.value > "" && devoluciones.value >= "") {
            Button(
                onClick = {
                    viewModel.thirdOperationInCourse.value = false
                    if (viewModel.imageName.value != null) {
                        val recibidos = viewModel.operationScaneed?.cantidad
                        if (recibidos == null) {
                            Toast.makeText(
                                context,
                                "No hay paquetes recibidos. Error de flujo.",
                                Toast.LENGTH_LONG,
                            ).show()
                        } else {
                            val entregs = entregados.value.toInt()
                            val devolus = devoluciones.value.toInt()
                            if (entregs + devolus > recibidos || entregs + devolus < recibidos) {
                                Toast.makeText(
                                    context,
                                    "Las entregas y devoluciones no son validas. No coinciden con el numero de paquetes recibidos recibidos",
                                    Toast.LENGTH_LONG,
                                ).show()
                            }else {
                                sendUpdate(viewModel, end = true, entregados = entregados.value, devoluciones = devoluciones.value, token =  token, context = context)
                                entregados.value = ""
                                devoluciones.value = ""
                                viewModel.getData.value = true
                                viewModel.operationSelected = null
                                viewModel.dataFromSelectedItem.value = null
                                viewModel.keepData.value = false
                                viewModel.operationScaneed = null
                                viewModel.dataFromSelectedItem.value = null
                                onNavigateToHome()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Sube una imagen de evidencia para continuar", Toast.LENGTH_LONG).show()
                    }},
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
            viewModel,
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
    val tokenManager = TokenManager(LocalContext.current)
    val repartidorId = tokenManager.getUserId()?.toInt()

    BackHandler {
        viewModel.tipoOperacion.value = null
        viewModel.imageName.value = null
        viewModel.imageCompressed.value = null
        viewModel.imageUri.value = null
        viewModel.recibidos.value = ""
        viewModel.entregados.value = ""
        viewModel.devoluciones.value = ""
        viewModel.getData.value = true
        navController.navigate("home_screen")
    }

    var operationStatus = remember { mutableStateOf("") }
    val opIdDialog = remember { mutableStateOf(true) }
    val success = remember { mutableStateOf(false) }
    val operationData = remember { mutableStateOf<OperationApiResponse?>(null) }
    var canUpdateStatus = remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    viewModel.onBackfromScanScreen.value = "third_screen"

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

    selectedItem = if (viewModel.thirdOperationInCourse.value == true || operationStatus.value == "en ruta") {
        "Finalizar ruta"
    } else {
        "Iniciar ruta"
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
            Banner(navController, viewModel)
            TitleText(title = "Operaciones Terceros")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(scrollState)
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
                    } else if(repartidorId != parsedData.repartidor) {
                        selectedItem = "No repartidor match"
                    }else {
                        Text(
                            text = "${parsedData.id_tipo_operacion} : ${parsedData.codigo}",
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF213E85)
                        )
                        //CallButton(phoneNumber = parsedData.numero_referencia)


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
                                        println("click: ${selectedItem}")
                                        selectedItem = "Iniciar ruta"
                                        expanded = false
                                        println("click: ${selectedItem}")
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text(text = "Finalizar ruta")
                                    },
                                    onClick = {
                                        println("click: ${selectedItem}")
                                        selectedItem = "Finalizar ruta"
                                        expanded = false
                                        println("click: ${selectedItem}")
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
                            StartRoute(
                                {
                                    viewModel.imageName.value = null
                                    viewModel.imageCompressed.value = null
                                    viewModel.imageUri.value = null
                                    navController.navigate("home_screen") {
                                        launchSingleTop = true // Evita duplicados en la pila
                                        restoreState = false   // Ignora el estado guardado
                                        popUpTo("home_screen") { inclusive = true }
                                    }
                                }, {
                                viewModel.keepData.value = true
                                navController.navigate("photo_screen")
                            }, viewModel)
                        }
                    }
                    "Finalizar ruta" -> {
                        if (parsedData != null) {
                            EndRoute(
                                {
                                    viewModel.imageName.value = null
                                    viewModel.imageCompressed.value = null
                                    viewModel.imageUri.value = null
                                    navController.navigate("home_screen") {
                                        launchSingleTop = true // Evita duplicados en la pila
                                        restoreState = false   // Ignora el estado guardado
                                        popUpTo("home_screen") { inclusive = true }
                                    }
                                }, {
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

    LaunchedEffect(operationData.value) {
        viewModel.operationSelected = null
        viewModel.dataFromSelectedItem.value = null
    }
}

private fun sendUpdate(viewModel: OperationsViewModel, recibidos: String = "0", entregados: String = "0", devoluciones: String = "0", end: Boolean = false, token: String?, context: Context) {
    var opData = viewModel.operationScaneed
    var codigo = opData?.codigo
    val userManager = UserManager(context)
    val userName = userManager.getName()

    if (opData != null && codigo != null) {
        if (end) {
            opData.entregas = entregados.toInt()
            opData.devoluciones = devoluciones.toInt()
            opData.imagenOpcional = viewModel.imageCompressed.value
            opData.status = "efectiva"
        } else {
            opData.cantidad = recibidos.toInt()
            opData.imagen = viewModel.imageCompressed.value
            opData.status = "en ruta"
        }

        val nuevoMovimiento = Movimiento(
            fecha = System.currentTimeMillis(),
            status = opData.status,
            user = userName,
            descripcion = "Operacion actualizada por un repartidor desde el app movil"
        )

        var historial = opData.historial
        historial = historial?.plus(nuevoMovimiento)
        opData.historial = historial

        updateOperationStatus(codigo, opData, token)
        viewModel.keepData.value = false
        viewModel.operationScaneed = null
        viewModel.recibidos.value = ""
        viewModel.entregados.value = ""
        viewModel.devoluciones.value = ""
        viewModel.getData.value = true
    }
}