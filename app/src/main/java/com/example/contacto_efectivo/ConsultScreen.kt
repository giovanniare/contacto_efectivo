package com.example.contacto_efectivo

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController

@Composable
fun ConsultScreen(navController: NavController, viewModel: OperationsViewModel) {
    BackHandler {
        viewModel.getData.value = true
        navController.navigate("home_screen") {

            popUpTo("home_screen") { inclusive = true }
            popUpTo("consult_screen") { inclusive = true }
        }
    }
    val opIdDialog = remember { mutableStateOf(true) }
    val success = remember { mutableStateOf(false) }
    val data = remember { mutableStateOf<OperationApiResponse?>(null) }
    viewModel.onBackfromScanScreen.value = "consult_screen"

    if (viewModel.dataFromSelectedItem.value != null && viewModel.operationSelected != null) {
        success.value = true
        data.value = viewModel.operationSelected
        println("Data scanned: ${data.value}")
        opIdDialog.value = false
    } else if (opIdDialog.value) {
        AskOperationId(
            operationDialog = opIdDialog,
            success = success,
            data = data,
            onScanScreen = {navController.navigate("scan_screen")},
            navController = navController,
            viewModel = viewModel
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Banner(navController, viewModel)
        TitleText(title = "Consulta de estatus")
        Column(
            modifier = Modifier
                .padding(25.dp)
                .fillMaxWidth()
        ) {
            if (success.value) {
                ShowOperationDetails(operationData = data, viewModel = viewModel)
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
        }
    }

    LaunchedEffect(data.value) {
        viewModel.operationSelected = null
        viewModel.dataFromSelectedItem.value = null
    }
}

@Composable
private fun ShowOperationDetails(operationData: MutableState<OperationApiResponse?>, viewModel: OperationsViewModel) {
    val operacion = operationData.value

    if (operacion != null) {
        if (operacion.id_tipo_operacion == "terceros") {
            ThirdConsultView(operacion = operacion, viewModel = viewModel)
        }
        else {
            GenericConsultView(operacion = operacion, viewModel = viewModel)
        }
    } else {
        Text(text = "No se pudo cargar la operación.")
    }
}

@Composable
fun CallButton(operacion: OperationApiResponse, viewModel: OperationsViewModel) {
    val phoneNumber = operacion.numero_referencia ?: "N/A"
    val context = LocalContext.current
    val routeManager = RouteManager(context)
    routeManager.initializeLocationClient()

    Row (horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()){
        Button(
            onClick = {
                try {
                    // Crear el Intent para iniciar la app de llamadas
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:$phoneNumber")
                    }
                    // Iniciar la actividad de llamada
                    ContextCompat.startActivity(context, intent, null)
                } catch (e: Exception) {
                    Toast.makeText(context, "Numero invalido", Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF213E85)),
            shape = RoundedCornerShape(13.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = "Llamar",
                tint = Color.White
            )
            //Text(text = "Llamar", color = Color.White)
        }
        Button(
            onClick = {
                try {
                    // Crear el Intent para iniciar la app de llamadas
                    var fullNumber = ""
                    if (phoneNumber.contains("+52")){
                        fullNumber = phoneNumber
                    } else {
                        fullNumber = "+52$phoneNumber{}"
                    }

                    val uri = Uri.parse("https://api.whatsapp.com/send?phone=$fullNumber")
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    intent.setPackage("com.whatsapp")
                    // Iniciar la actividad de llamada
                    ContextCompat.startActivity(context, intent, null)
                } catch (e: Exception) {
                    Toast.makeText(context, "Un error ocurrio.", Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF213E85)),
            shape = RoundedCornerShape(13.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.whatsapp_white),
                contentDescription = "Whatsapp",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            //Text(text = "Mensaje", color = Color.White)
        }
        Button(
            onClick = {
                try {
                    println("Antes de la verificacion del viewModel")
                    println("Si entra al boton de la direccion")
                    routeManager.openDirection(operacion, viewModel)
                } catch (e: Exception) {
                    Toast.makeText(context, "Un error ocurrio.", Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF213E85)),
            shape = RoundedCornerShape(13.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Ubicacion",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            //Text(text = "Direccion", color = Color.White)
        }
    }
}

@Composable
private fun ThirdConsultView(operacion: OperationApiResponse, viewModel: OperationsViewModel) {
    val proveedores = viewModel.proveedores.value
    val proveedor: Proveedor? = proveedores.find { it.id == operacion.idProveedor }

    Column {
        Text(
            text = "Codigo:",
            fontSize = 20.sp,
            color = Color(0xFF213E85),
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily(Font(R.font.inter_extrabold)),
            modifier = Modifier
                .padding(top = 5.dp)
                .align(Alignment.CenterHorizontally)

        )
        Text(
            text = "${operacion.codigo}",
            fontSize = 20.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily(Font(R.font.inter_extrabold)),
            modifier = Modifier
                .padding(5.dp)
                .align(Alignment.CenterHorizontally)

        )
        Text(
            text = "Estatus:",
            fontSize = 20.sp,
            color = Color(0xFF213E85),
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily(Font(R.font.inter_extrabold)),
            modifier = Modifier
                .padding(top = 10.dp)
                .align(Alignment.CenterHorizontally)

        )
        Text(
            text = operacion.status,
            fontSize = 20.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily(Font(R.font.inter_extrabold)),
            modifier = Modifier
                .padding(5.dp)
                .align(Alignment.CenterHorizontally)

        )
        Text(
            text = "Informacion general del paquete",
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            color = Color(0xFF213E85),
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(top = 10.dp, bottom = 10.dp)
                .align(Alignment.CenterHorizontally)

        )
        Text(
            text = "Tipo: ${operacion.id_tipo_operacion}",
            fontSize = 18.sp,
            textAlign = TextAlign.Justify,
            color = Color(0xFF213E85),
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(start = 27.dp)

        )
        Text(
            text = "Proveedor: ${proveedor?.nombre}",
            fontSize = 18.sp,
            textAlign = TextAlign.Justify,
            color = Color(0xFF213E85),
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(start = 27.dp)

        )
        Text(
            text = "Fecha de Inicio:",
            fontSize = 18.sp,
            textAlign = TextAlign.Justify,
            color = Color(0xFF213E85),
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(start = 27.dp)

        )
        Text(
            text = operacion.fecha_inicio,
            fontSize = 18.sp,
            textAlign = TextAlign.Justify,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(start = 27.dp)

        )
        Text(
            text = "Comentario:",
            fontSize = 18.sp,
            textAlign = TextAlign.Justify,
            color = Color(0xFF213E85),
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(start = 27.dp, top = 5.dp)

        )
        Text(
            text = operacion.comentario ?: "N/A",
            fontSize = 18.sp,
            textAlign = TextAlign.Justify,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(start = 27.dp)

        )

    }
}


@Composable
private fun GenericConsultView(operacion: OperationApiResponse, viewModel: OperationsViewModel) {
    Column {
        Text(
            text = "Codigo:",
            fontSize = 20.sp,
            color = Color(0xFF213E85),
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily(Font(R.font.inter_extrabold)),
            modifier = Modifier
                .padding(top = 5.dp)
                .align(Alignment.CenterHorizontally)

        )
        Text(
            text = "${operacion.codigo}",
            fontSize = 20.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily(Font(R.font.inter_extrabold)),
            modifier = Modifier
                .padding(5.dp)
                .align(Alignment.CenterHorizontally)

        )
        Text(
            text = "Estatus: ${operacion.status}",
            fontSize = 20.sp,
            color = Color(0xFF213E85),
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily(Font(R.font.inter_extrabold)),
            modifier = Modifier
                .padding(top = 10.dp)
                .align(Alignment.CenterHorizontally)

        )
        Text(
            text = "Importe: $${operacion.precio}",
            fontSize = 18.sp,
            textAlign = TextAlign.Justify,
            color = Color(0xFF213E85),
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(top = 10.dp, bottom = 10.dp)
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = "Informacion general del paquete",
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            color = Color(0xFF213E85),
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(top = 10.dp, bottom = 10.dp)
                .align(Alignment.CenterHorizontally)

        )
        Text(
            text = "Tipo: ${operacion.id_tipo_operacion}",
            fontSize = 18.sp,
            textAlign = TextAlign.Justify,
            color = Color(0xFF213E85),
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(start = 27.dp)

        )
        Text(
            text = "Dirección de entrega:",
            fontSize = 18.sp,
            textAlign = TextAlign.Justify,
            color = Color(0xFF213E85),
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(start = 27.dp)

        )
        Text(
            text = operacion.direccion_final ?: "N/A",
            fontSize = 18.sp,
            textAlign = TextAlign.Justify,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(start = 27.dp)

        )
        Text(
            text = "Cliente:",
            fontSize = 18.sp,
            textAlign = TextAlign.Justify,
            color = Color(0xFF213E85),
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(start = 27.dp, top = 5.dp)

        )
        Text(
            text = operacion.nombre_referencia ?: "N/A",
            fontSize = 18.sp,
            textAlign = TextAlign.Justify,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(start = 27.dp)

        )
        CallButton(operacion, viewModel)
    }
}