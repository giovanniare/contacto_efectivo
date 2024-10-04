package com.example.contacto_efectivo

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Banner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(135.dp)
            .clip(RoundedCornerShape(bottomEnd = 75.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(Color(0xFF000A7B), Color(0xFFE8D67E))
                )
            )
    ) {
        Text(
            text = "Contacto Efectivo",
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily(Font(R.font.inter_extrabold)),
            color = Color.White,
            modifier = Modifier.padding(start = 15.dp, top = 23.dp)
        )
    }
}

@Composable
fun TitleText(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 35.dp, bottom = 35.dp)
    ) {
        Text(
            text = title,
            fontSize = 34.sp,
            lineHeight = 37.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily(Font(R.font.inter_extrabold)),
            color = Color(0xFF213E85)
        )
    }
}

@Composable
fun TomarEvidencia(
    operationDialog: MutableState<Boolean>,
    onNavigateToGallery: () -> Unit,
    onPhotoScreen: () -> Unit)
{

    if (operationDialog.value) {
        val context = LocalContext.current
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let {
                val imagePath = getRealPathFromURI(context, it)
                uploadImage(context, imagePath) // Sube la imagen seleccionada
                operationDialog.value = false
            } ?: run {
                Toast.makeText(
                    context,
                    "Error al seleccionar la imagen",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        AlertDialog(
            onDismissRequest = { operationDialog.value = false},
            title = { Text(text = "Adjunta evidencia") },
            text = {
                Column {
                    Text("Toma una foto o subela desde tu galeria")
                    Button(
                        onClick = {
                            launcher.launch("image/*")
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
                    Button(
                        onClick = { onPhotoScreen() },
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Text(text = "Camara")
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Foto",
                            tint = Color(0xFF213E85)
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = {  }) {
                    Text("Buscar")
                }
            },
            dismissButton = {
                Button(onClick = { operationDialog.value = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

}