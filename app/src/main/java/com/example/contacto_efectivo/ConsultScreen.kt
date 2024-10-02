package com.example.contacto_efectivo

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
        navController.navigate("home_screen")
    }
    val opIdDialog = remember { mutableStateOf(true) }
    val success = remember { mutableStateOf(false) }
    val data = remember { mutableStateOf<String?>(null) }

    if (opIdDialog.value) {
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
        Banner()
        TitleText(title = "Consulta de estatus")
        Column(
            modifier = Modifier
                .padding(25.dp)
                .fillMaxWidth()
        ) {
            if (success.value) {
                ShowOperationDetails(operationData = data)
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
}

@Composable
private fun ShowOperationDetails(operationData: MutableState<String?>) {
    val operacion = operationData.value?.let { parseJsonToOperacion(it) }

    operacion?.let {
        Column {
            Text(
                text = "ID de Operacion:",
                fontSize = 20.sp,
                color = Color(0xFF213E85),
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.inter_extrabold)),
                modifier = Modifier
                    .padding(top = 10.dp)
                    .align(Alignment.CenterHorizontally)

            )
            Text(
                text = "${it.codigo}",
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
                text = it.status,
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
                text = "Dirección de Inicio:",
                fontSize = 18.sp,
                textAlign = TextAlign.Justify,
                color = Color(0xFF213E85),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(start = 27.dp)

            )
            Text(
                text = it.direccion_inicio,
                fontSize = 18.sp,
                textAlign = TextAlign.Justify,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(start = 27.dp)

            )
            Text(
                text = "Dirección Final:",
                fontSize = 18.sp,
                textAlign = TextAlign.Justify,
                color = Color(0xFF213E85),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(start = 27.dp, top = 5.dp)

            )
            Text(
                text = it.direccion_final,
                fontSize = 18.sp,
                textAlign = TextAlign.Justify,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(start = 27.dp)

            )
            Text(
                text = "Nombre de Referencia:",
                fontSize = 18.sp,
                textAlign = TextAlign.Justify,
                color = Color(0xFF213E85),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(start = 27.dp, top = 5.dp)

            )
            Text(
                text = it.nombre_referencia,
                fontSize = 18.sp,
                textAlign = TextAlign.Justify,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(start = 27.dp)

            )

            CallButton(phoneNumber = it.numero_referencia)
        }
    } ?: run {
        Text(text = "No se pudo cargar la operación.")
    }
}

@Composable
fun CallButton(phoneNumber: String) {
    val context = LocalContext.current

    Button(
        onClick = {
            // Crear el Intent para iniciar la app de llamadas
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$phoneNumber")
            }
            try {
                // Iniciar la actividad de llamada
                ContextCompat.startActivity(context, intent, null)
            } catch (e: Exception) {
                e.printStackTrace()
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
        Text(text = "Llamar", color = Color.White)
    }
}