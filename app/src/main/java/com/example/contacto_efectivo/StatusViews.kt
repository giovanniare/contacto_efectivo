package com.example.contacto_efectivo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import kotlinx.coroutines.delay

@Composable
fun StatusView(
    succeed: Boolean,
    sentece: String = "Esto es una prueba de la pantalla de animacion",
    viewModel: OperationsViewModel,
    navController: NavController,
) {
    var animation = R.raw.error
    if (succeed) {
        animation = R.raw.success
    }

    LaunchedEffect(Unit) {
        println("status screen launched")
        println("next screen: ${viewModel.nextStatusScreen.value}")
        delay(3000)
        val nextScreen = viewModel.nextStatusScreen.value
        viewModel.nextStatusScreen.value = ""
        viewModel.statusMessage.value = ""
        navController.navigate(nextScreen)
    }

    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(animation)
    )
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        LottieAnimation(
            composition = composition
        )
        Text(text = sentece)
    }
}

@Composable
fun ThinkingView(
    viewModel: OperationsViewModel,
    navController: NavController,
    tokenManager: TokenManager
) {
    //val operationScanned by viewModel.optScannedFromMultiScan.collectAsState()
    val userInput = viewModel.operationId
    val context = LocalContext.current
    val userManager = UserManager(context)
    val httpRequests = HttpRequests()

    LaunchedEffect(Unit) {
        val token = tokenManager.getToken()
        val userName = userManager.getName()
        val op = httpRequests.getOperation("operacion/${userInput.value}/codigo/", token)

        if (op == null) {
            println("Entra al operationScaneed === null")
            viewModel.statusMessage.value = "La operación no se encuentra registrada"
            viewModel.nextStatusScreen.value = "multi_barcode_scan_screen"
            vibratePhone(context, 700)
            navController.navigate("error_view")
        } else {
            val userId = tokenManager.getUserId()
            if (op.repartidor != userId?.toInt()) {
                println("La operacion esta asignada a otro repartidor")
                viewModel.nextStatusScreen.value = "multi_barcode_scan_screen"
                viewModel.statusMessage.value = "Esta operacion esta asignada a otro repartidor"
                vibratePhone(context, 700)
                navController.navigate("error_view")
            }
            else if (op.status != "asignada") {
                viewModel.nextStatusScreen.value = "multi_barcode_scan_screen"
                viewModel.statusMessage.value = "No es posible volver a enrutar esta operacion"
                vibratePhone(context, 700)
                navController.navigate("error_view")
            } else {
                var historial = op.historial
                val nuevoMovimiento = Movimiento(
                    fecha = System.currentTimeMillis(),
                    status = "en ruta",
                    user = userName,
                    descripcion = "Operacion actualizada por un repartidor desde el app movil"
                )

                historial = historial?.plus(nuevoMovimiento)
                op.historial = historial
                op.status = "en ruta"

                updateOperationStatus(op.codigo!!, op, token)

                viewModel.nextStatusScreen.value = "multi_barcode_scan_screen"
                viewModel.statusMessage.value = "Actualizacion de Operacion ${userInput.value} EXISOTSA - Estatus: En ruta"
                navController.navigate("success_view")
            }
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .width(64.dp)
                .align(Alignment.CenterHorizontally),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }



}
