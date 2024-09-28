package com.example.contacto_efectivo

import android.Manifest
import android.content.pm.PackageManager
import android.util.Size
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors


@Composable
fun BarcodeScannerScreen(onNavigateToHome: () -> Unit) {
    BackHandler {
        onNavigateToHome()
    }

    val context = LocalContext.current

    // Solicitar permisos de cámara
    val cameraPermissionGranted = remember { mutableStateOf(false) }
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        cameraPermissionGranted.value = isGranted
    }

    // Comprobar si tenemos permisos de cámara
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
        LaunchedEffect(Unit) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    } else {
        cameraPermissionGranted.value = true
    }

    // Mostrar la cámara si tenemos permisos
    if (cameraPermissionGranted.value) {
        CameraPreviewWithBarcodeScanner()
    } else {
        Text("Necesitamos acceso a la cámara para escanear códigos.")
    }
}

@OptIn(ExperimentalGetImage::class)
@Composable
fun CameraPreviewWithBarcodeScanner() {
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient()
    val executor = remember { Executors.newSingleThreadExecutor() }

    // Estado para almacenar el valor del código escaneado
    val scannedCode = remember { mutableStateOf<String?>(null) }

    // Variable para almacenar el proveedor de la cámara para su liberación posterior
    var cameraProvider: ProcessCameraProvider? by remember { mutableStateOf(null) }

    // Liberar la cámara cuando el Composable se destruye
    DisposableEffect(Unit) {
        onDispose {
            cameraProvider?.unbindAll()
        }
    }

    LaunchedEffect(Unit) {
        cameraProvider = cameraProviderFuture.get() // Guardamos el proveedor de la cámara
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Vista previa de la cámara
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val imageCapture = ImageCapture.Builder().build()
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
                                            scannedCode.value = barcode.url?.url
                                        }
                                        Barcode.TYPE_TEXT -> {
                                            scannedCode.value = barcode.displayValue
                                        }
                                    }
                                }
                            }
                            .addOnFailureListener {
                                // Error al procesar
                                Toast.makeText(ctx, "Error al escanear código", Toast.LENGTH_SHORT).show()
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
                        imageCapture,
                        imageAnalyzer
                    )
                } catch (e: Exception) {
                    Toast.makeText(ctx, "Error inicializando la cámara", Toast.LENGTH_SHORT).show()
                }

                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // Mostramos el valor del código escaneado
        scannedCode.value?.let {
            Text(
                text = "Código escaneado: $it",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }
    }
}


// ***********************************************************************************************************************
// ***********************************************************************************************************************
// ***********************************************************************************************************************
// ***********************************************************************************************************************

