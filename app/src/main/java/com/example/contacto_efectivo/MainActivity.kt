package com.example.contacto_efectivo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.contacto_efectivo.ui.theme.Contacto_efectivoTheme



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
            ThirdScreen(onNavigateToHome = {
                navController.navigate("home_screen")
            })
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
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ScreenPreview() {
    Contacto_efectivoTheme {
        MyApp()
    }
}