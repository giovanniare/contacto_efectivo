package com.example.contacto_efectivo

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.contacto_efectivo.ui.theme.Contacto_efectivoTheme
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class MainActivity : ComponentActivity() {

    private lateinit var imageCapture: ImageCapture
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Configura el directorio de salida para las imágenes
        outputDirectory = getOutputDirectory()

        // Ejecutores para manejar los hilos de la cámara
        cameraExecutor = Executors.newSingleThreadExecutor()

        setContent {
            Contacto_efectivoTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    MaterialTheme(){
                        MyApp()
                    }

                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown() // Cerrar el hilo de la cámara cuando la actividad se destruya
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }
}

@Composable
fun MyApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login")
    {
        composable("login") {
            LogInScreen(onNavigateToHome = {
                navController.navigate("home_screen")
            })
        }
        composable("home_screen") {
            HomeScreen(navController, "Giovanni Arellano")
        }
        composable("consult_screen") {
            ConsultScreen(onNavigateToHome = {
                navController.navigate("home_screen")
            })
        }
        composable("third_screen") {
            ThirdScreen(
                onNavigateToHome = { navController.navigate("home_screen") },
                onNavigateToPhoto = { navController.navigate("photo_screen")}
            )
        }
        composable("update_screen") {
            UpdateScreen(onNavigateToHome = {
                navController.navigate("home_screen")
            })
        }
        composable("scan_screen") {
            BarcodeScannerScreen(onNavigateToHome = {
                navController.navigate("home_screen")
            })
        }
        composable("photo_screen") {
            CameraCaptureScreen(navController)
        }
        composable("preview/{imagePath}") { backStackEntry ->
            val context = LocalContext.current
            val imagePath = backStackEntry.arguments?.getString("imagePath")

            if (!imagePath.isNullOrEmpty()) {
                // Decodificar la imagen desde el path
                val bitmap = remember { BitmapFactory.decodeFile(imagePath) }

                // Variable de estado para almacenar el cameraProvider
                val cameraProviderState = remember { mutableStateOf<ProcessCameraProvider?>(null) }

                // Efecto lanzado para obtener el cameraProvider
                LaunchedEffect(Unit) {
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                    cameraProviderFuture.addListener({
                        cameraProviderState.value = cameraProviderFuture.get()
                    }, ContextCompat.getMainExecutor(context))
                }

                // Mostrar la pantalla de vista previa
                cameraProviderState.value?.let { cameraProvider ->
                    PreviewScreen(
                        imageBitmap = bitmap,
                        imagePath = imagePath,
                        onUpload = { uploadImage(context, imagePath) }, // Sube la imagen usando el path
                        onRetry = { navController.popBackStack() }, // Vuelve a la cámara
                        onCancel = {
                            // Cierra el proceso y libera los recursos de la cámara
                            cameraProvider.unbindAll()
                            navController.popBackStack("photo_screen", false)
                        },
                        cameraProvider = cameraProvider
                    )
                }
            } else {
                // Manejar el caso en que el imagePath sea nulo
                Toast.makeText(context, "Error al cargar la imagen", Toast.LENGTH_SHORT).show()
                navController.popBackStack() // Vuelve a la pantalla anterior
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ScreenPreview() {
    Contacto_efectivoTheme {
        MyApp()
    }
}