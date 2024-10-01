@file:Suppress("NAME_SHADOWING")

package com.example.contacto_efectivo

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun UploadPhotoScreen() {
    val context = LocalContext.current

    // Lanza el lanzador de la actividad para seleccionar una imagen
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val imagePath = getRealPathFromURI(context, it)
            uploadImage(context, imagePath) // Sube la imagen seleccionada
        } ?: run {
            Toast.makeText(context, "Error al seleccionar la imagen", Toast.LENGTH_SHORT).show()
        }
    }


    // Tu botón que llamará a la función openGallery
    Column {
        Button(
            onClick = {
                launcher.launch("image/*") // Inicia el lanzador para seleccionar la imagen
            },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Galeria")
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Cargar",
                tint = Color(0xFF213E85)
            )
        }
    }

}

fun getRealPathFromURI(context: Context, uri: Uri): String? {
    var result: String? = null
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    if (cursor != null) {
        if (cursor.moveToFirst()) {
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            result = cursor.getString(idx)
        }
        cursor.close()
    }
    return result
}

fun uploadImage(context: Context, imagePath: String?) {
    // Verifica que el imagePath no sea nulo
    if (imagePath != null) {
        // Lógica para subir la imagen seleccionada
        Toast.makeText(context, "Subiendo imagen desde: $imagePath", Toast.LENGTH_SHORT).show()
    } else {
        Toast.makeText(context, "Error al seleccionar la imagen", Toast.LENGTH_SHORT).show()
    }
}
