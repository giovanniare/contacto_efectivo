package com.example.contacto_efectivo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CameraCaptureScreen(navController: NavController) {
    val context = LocalContext.current
    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { mutableStateOf<ImageCapture?>(null) }
    val showPreview = remember { mutableStateOf(false) }
    val imageBitmap = remember { mutableStateOf<Bitmap?>(null) }
    val imagePath = remember { mutableStateOf("") }
    val cameraProviderState = remember { mutableStateOf<ProcessCameraProvider?>(null) }

    // Configurar cámara y obtener el cameraProvider
    LaunchedEffect(Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            cameraProviderState.value = cameraProvider // Guardar el cameraProvider
            setupCamera(context, previewView, imageCapture, cameraProvider)
        }, ContextCompat.getMainExecutor(context))
    }

    if (showPreview.value) {
        // Mostrar vista previa con botones
        cameraProviderState.value?.let { cameraProvider ->
            PreviewScreen(
                imageBitmap = imageBitmap.value,
                imagePath = imagePath.value,
                onUpload = {
                    uploadImage(context, imagePath.value)
                    cameraProvider.unbindAll() // Liberar recursos al subir la imagen
                    navController.popBackStack()
                },
                onRetry = {
                    // Reintentar
                    showPreview.value = false
                    setupCamera(context, previewView, imageCapture, cameraProvider)
                },
                onCancel = {
                    cameraProvider.unbindAll() // Liberar recursos al cancelar
                    navController.popBackStack()
                },
                cameraProvider = cameraProvider
            )
        }
    } else {
        // Vista de la cámara
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = {
                    previewView.apply {
                        layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Botón para capturar la imagen
            Button(
                onClick = {
                    val photoFile = createFile(context)
                    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                    imageCapture.value?.takePicture(
                        outputOptions,
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                val savedUri = outputFileResults.savedUri ?: Uri.fromFile(photoFile)
                                // Obtener el bitmap de la imagen capturada
                                imageBitmap.value = BitmapFactory.decodeFile(photoFile.absolutePath)
                                imagePath.value = photoFile.absolutePath // Guardar el path
                                showPreview.value = true // Mostrar la vista previa
                            }

                            override fun onError(exception: ImageCaptureException) {
                                // Manejar el error
                                Toast.makeText(
                                    context,
                                    "Error al guardar la imagen: ${exception.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    )
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Text("Tomar foto")
            }
        }
    }
}

// Función para crear un archivo para guardar la imagen
private fun createFile(context: Context): File {
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile("JPEG_$timeStamp", ".jpg", storageDir)
}

// Función para subir la imagen
fun uploadImage(context: Context, imagePath: String) {
    // Lógica para subir la imagen
    Toast.makeText(context, "Subiendo imagen desde: $imagePath", Toast.LENGTH_SHORT).show()
}

@Composable
fun PreviewScreen(
    imageBitmap: Bitmap?,
    imagePath: String,
    onUpload: () -> Unit,
    onRetry: () -> Unit,
    onCancel: () -> Unit,
    cameraProvider: ProcessCameraProvider
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        imageBitmap?.let {
            Image(bitmap = it.asImageBitmap(), contentDescription = null)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = {
                onUpload()
                cameraProvider.unbindAll() // Liberar los recursos de la cámara
            }) {
                Text("Subir")
            }
            Button(onClick = { onRetry() }) {
                Text("Reintentar")
            }
            Button(onClick = {
                onCancel()
                cameraProvider.unbindAll() // Liberar los recursos de la cámara
            }) {
                Text("Cancelar")
            }
        }
    }
}

private fun setupCamera(
    context: Context,
    previewView: PreviewView,
    imageCapture: MutableState<ImageCapture?>,
    cameraProvider: ProcessCameraProvider
) {
    // Crear un caso de uso de vista previa
    val preview = Preview.Builder()
        .build()
        .also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

    // Crear un caso de uso de captura de imagen
    imageCapture.value = ImageCapture.Builder()
        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
        .build()

    // Seleccionar la cámara trasera
    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    try {
        // Desvincular cualquier caso de uso que esté vinculado antes de vincular nuevos
        cameraProvider.unbindAll()

        // Vincular los casos de uso
        cameraProvider.bindToLifecycle(
            context as LifecycleOwner,
            cameraSelector,
            preview,
            imageCapture.value
        )
    } catch (exc: Exception) {
        Log.e("CameraXApp", "Error al vincular casos de uso", exc)
    }
}
