package com.example.contacto_efectivo

import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun RequestPermissionsScreen(onNavigateToHome: () -> Unit) {
    val context = LocalContext.current
    val permissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.CALL_PHONE,
        //Manifest.permission.READ_EXTERNAL_STORAGE,
        //Manifest.permission.WRITE_EXTERNAL_STORAGE,
        //Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsResult ->
        val allGranted = permissionsResult.all { it.value }
        if (allGranted) {
            Toast.makeText(context, "Todos los permisos concedidos", Toast.LENGTH_SHORT).show()
            onNavigateToHome()
        } else {
            Toast.makeText(context, "Algunos permisos fueron denegados", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = { permissionLauncher.launch(permissions) }) {
            Text("Solicitar Permisos")
        }
    }
}

fun checkPermissions(context: Context): Boolean {
    val requiredPermissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.CALL_PHONE,
        //Manifest.permission.READ_EXTERNAL_STORAGE,
        //Manifest.permission.WRITE_EXTERNAL_STORAGE,
        //Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    return requiredPermissions.all {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
}