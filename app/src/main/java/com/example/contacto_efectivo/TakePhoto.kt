package com.example.contacto_efectivo

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
fun CameraCaptureScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalContext.current as LifecycleOwner
    val imageCapture = remember { mutableStateOf<ImageCapture?>(null) }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val executor: ExecutorService = remember { Executors.newSingleThreadExecutor() }

    // Estado para permisos de cámara
    val cameraPermissionGranted = remember { mutableStateOf(false) }
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        cameraPermissionGranted.value = isGranted
    }

    // Solicitar permisos de cámara
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
        LaunchedEffect(Unit) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    } else {
        cameraPermissionGranted.value = true
    }

    // Si se otorgan los permisos, mostramos la vista previa de la cámara
    if (cameraPermissionGranted.value) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            CameraPreview(imageCapture, cameraProviderFuture, executor, lifecycleOwner)

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                imageCapture.value?.let { capturePhoto(context, it, executor) }
            }) {
                Text("Tomar Foto")
            }
        }
    } else {
        Text("Permiso de cámara no otorgado.")
    }
}

@Composable
fun CameraPreview(
    imageCapture: MutableState<ImageCapture?>,
    cameraProviderFuture: ListenableFuture<ProcessCameraProvider>,
    executor: ExecutorService,
    lifecycleOwner: LifecycleOwner
) {
    val context = LocalContext.current
    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val capture = ImageCapture.Builder()
                .setTargetRotation(previewView.display.rotation)
                .build()

            imageCapture.value = capture

            try {
                val cameraProvider = cameraProviderFuture.get()
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,  // Cambio importante
                    cameraSelector,
                    preview,
                    capture
                )
            } catch (e: Exception) {
                Toast.makeText(context, "Error iniciando la cámara: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }

            previewView
        },
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(3f / 4f)
    )
}

fun capturePhoto(context: Context, imageCapture: ImageCapture, executor: ExecutorService) {
    val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
        .format(System.currentTimeMillis())
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Photos")
        }
    }

    val outputOptions = ImageCapture.OutputFileOptions.Builder(
        context.contentResolver,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        contentValues
    ).build()

    imageCapture.takePicture(
        outputOptions,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                Toast.makeText(context, "Foto guardada correctamente", Toast.LENGTH_SHORT).show()
            }

            override fun onError(exception: ImageCaptureException) {
                Toast.makeText(context, "Error al guardar foto: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
    )
}
