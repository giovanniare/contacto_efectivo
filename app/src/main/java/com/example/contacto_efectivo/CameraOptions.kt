package com.example.contacto_efectivo

import android.util.Size
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import kotlin.io.path.OnErrorResult

@OptIn(ExperimentalGetImage::class)
@Composable
fun BarcodeScannerScreen(
    viewModel: OperationsViewModel, // Obtén una instancia del ViewModel
    onCodeScanned: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit = {},
    multiscan: Boolean = false
) {
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient()
    val executor = remember { Executors.newSingleThreadExecutor() }

    var cameraProvider: ProcessCameraProvider? by remember { mutableStateOf(null) }

    DisposableEffect(Unit) {
        onDispose {
            cameraProvider?.unbindAll()
        }
    }

    LaunchedEffect(Unit) {
        cameraProvider = cameraProviderFuture.get()
    }
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)

                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val imageAnalyzer = ImageAnalysis.Builder()
                        .setTargetResolution(Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                    imageAnalyzer.setAnalyzer(executor) { imageProxy ->
                        val mediaImage = imageProxy.image
                        if (mediaImage != null) {
                            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                            barcodeScanner.process(image)
                                .addOnSuccessListener { barcodes ->
                                    for (barcode in barcodes) {
                                        when (barcode.valueType) {
                                            Barcode.TYPE_URL -> {
                                                viewModel.operationIdUrl.value = barcode.url?.url ?: ""
                                                onCodeScanned(viewModel.operationIdUrl.value!!)
                                                cameraProvider?.unbindAll()
                                                onNavigateBack()
                                            }
                                            Barcode.TYPE_TEXT -> {
                                                viewModel.operationId.value = barcode.displayValue ?: ""
                                                println("Si estoy leyendo desde la pantalla scan: ${viewModel.operationId.value}")
                                                onCodeScanned(viewModel.operationId.value!!)
                                                cameraProvider?.unbindAll()
                                                onNavigateBack()
                                            }
                                            else -> {
                                                // 🔹 Extra: también soportamos QR explícitamente por formato
                                                if (barcode.format == Barcode.FORMAT_QR_CODE) {
                                                    viewModel.operationId.value = barcode.displayValue ?: ""
                                                    println("Si estoy leyendo un QR desde la pantalla scan: ${viewModel.operationId.value}")
                                                    onCodeScanned(viewModel.operationId.value!!)
                                                    cameraProvider?.unbindAll()
                                                    onNavigateBack()
                                                }
                                            }
                                        }
                                    }
                                }
                                .addOnCompleteListener {
                                    imageProxy.close()
                                }
                        }
                    }


                    try {
                        cameraProvider = cameraProviderFuture.get()
                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                        cameraProvider?.bindToLifecycle(
                            ctx as androidx.lifecycle.LifecycleOwner,
                            cameraSelector,
                            preview,
                            imageAnalyzer
                        )
                    } catch (e: Exception) {
                        Toast.makeText(ctx, "Error inicializando la cámara", Toast.LENGTH_SHORT).show()
                    }

                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(40.dp)
            ) {

                Button(
                    onClick = { onNavigateToHome() },
                    shape = RoundedCornerShape(13.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF213E85)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            BorderStroke(1.dp, Color(0xFFE8D67E)),
                            shape = RoundedCornerShape(13.dp)
                        )
                ) {
                    Text(
                        text = "Terminar de escanear",
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily(Font(R.font.inter_extrabold)),
                        color = Color.White
                    )
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Entrega a domicilio",
                        tint = Color.White
                    )
                }
            }
        }
    }

}


/*
@OptIn(ExperimentalGetImage::class)
@Composable
fun MultiBarcodeScannerScreen(
    viewModel: OperationsViewModel, // Obtén una instancia del ViewModel
    onCodeScanned: (String) -> Unit,
    errorResult: () -> Unit,
    successResult: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient()
    val executor = remember { Executors.newSingleThreadExecutor() }

    var cameraProvider: ProcessCameraProvider? by remember { mutableStateOf(null) }

    DisposableEffect(Unit) {
        onDispose {
            cameraProvider?.unbindAll()
        }
    }

    LaunchedEffect(Unit) {
        cameraProvider = cameraProviderFuture.get()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val imageAnalyzer = ImageAnalysis.Builder()
                    .setTargetResolution(Size(1280, 720))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                imageAnalyzer.setAnalyzer(executor) { imageProxy ->
                    val mediaImage = imageProxy.image
                    if (mediaImage != null) {
                        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                        barcodeScanner.process(image)
                            .addOnSuccessListener { barcodes ->
                                for (barcode in barcodes) {
                                    when (barcode.valueType) {
                                        Barcode.TYPE_URL -> {
                                            viewModel.operationIdUrl.value = barcode.url?.url ?: ""
                                            onCodeScanned(viewModel.operationIdUrl.value!!)
                                            cameraProvider?.unbindAll()
                                            successResult()
                                        }
                                        Barcode.TYPE_TEXT -> {
                                            viewModel.operationId.value = barcode.displayValue ?: ""
                                            println("Si estoy leyendo desde la pantalla scan: ${viewModel.operationId.value}")
                                            onCodeScanned(viewModel.operationId.value!!)
                                            cameraProvider?.unbindAll()
                                            successResult()
                                        }
                                    }
                                }
                            }
                            .addOnCompleteListener {
                                imageProxy.close()
                            }
                    }
                }

                try {
                    cameraProvider = cameraProviderFuture.get()
                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    cameraProvider?.bindToLifecycle(
                        ctx as androidx.lifecycle.LifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalyzer
                    )
                } catch (e: Exception) {
                    Toast.makeText(ctx, "Error inicializando la cámara", Toast.LENGTH_SHORT).show()
                }

                previewView
            },
            modifier = Modifier.fillMaxSize()
        )
        Box(modifier = Modifier.fillMaxSize()) {
            Button(
                onClick = { onNavigateToHome() },
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
                    text = "Terminar de escanear",
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.inter_extrabold)),
                    color = Color(0xFF213E85)
                )
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Entrega a domicilio",
                    tint = Color(0xFF213E85)
                )
            }

        }
    }
}
*/