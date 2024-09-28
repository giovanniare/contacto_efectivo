package com.example.contacto_efectivo

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun TomarEvidencia(
    operationDialog: MutableState<Boolean>,
    onNavigateToGallery: () -> Unit,
    onPhotoScreen: () -> Unit)
{

    if (operationDialog.value) {
        AlertDialog(
            onDismissRequest = { operationDialog.value = false},
            title = { Text(text = "Adjunta evidencia") },
            text = {
                Column {
                    Text("Toma una foto o subela desde tu galeria")
                    Button(
                        onClick = {  },
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
                }},
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

@SuppressLint("ResourceAsColor")
@Composable
private fun StartRoute(onNavigateToHome: () -> Unit, onNavigateToPhoto: () -> Unit) {
    var count by remember { mutableIntStateOf(0) }
    val opIdDialog = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 7.dp, end = 7.dp, top = 29.dp)
    ) {
        Row(modifier = Modifier.padding(bottom = 16.dp)){
            Text(
                text = "Paquetes Recibidos",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.blue_btn),
                modifier = Modifier
                    .weight(2f)
                    .align(Alignment.CenterVertically)
            )
            TextField(
                value = count.toString(),
                onValueChange = { count = it.toIntOrNull() ?: 0 },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(13.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = Color(0xFFe1e6ed)
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)

            )
        }
        Row(modifier = Modifier.padding(bottom = 16.dp, top = 16.dp)){
            Text(
                text = "Adjunta evidencia",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.blue_btn),
                modifier = Modifier
                    .weight(2f)
                    .align(Alignment.CenterVertically)
            )
            Button(
                onClick = { opIdDialog.value = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Filled.AddCircle,
                    contentDescription = "Tomar foto",
                    tint = Color(0xFF213E85)
                )
            }
        }
        Button(
            onClick = { onNavigateToHome() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF213E85)),
            shape = RoundedCornerShape(13.dp),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .height(65.dp)
                .padding(top = 16.dp)
        ) {
            Text(
                text = "Iniciar Ruta",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }

    if (opIdDialog.value) {
        TomarEvidencia(
            operationDialog = opIdDialog,
            onNavigateToGallery = { /* Aquí va la lógica al presionar el botón */ },
            onPhotoScreen = { onNavigateToPhoto() }
        )
    }
}

@Composable
private fun EndRoute(onNavigateToHome: () -> Unit, onNavigateToPhoto: () -> Unit) {
    var entregados by remember { mutableIntStateOf(0) }
    var devoluciones by remember { mutableIntStateOf(0) }
    val opIdDialog = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 7.dp, end = 7.dp, top = 29.dp)
    ) {
        Row(modifier = Modifier.padding(bottom = 16.dp)){
            Text(
                text = "Paquetes Entregados",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.blue_btn),
                modifier = Modifier
                    .weight(2f)
                    .align(Alignment.CenterVertically)
            )
            TextField(
                value = entregados.toString(),
                onValueChange = { entregados = it.toIntOrNull() ?: 0 },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(13.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = Color(0xFFe1e6ed)
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)

            )
        }
        Row(modifier = Modifier.padding(bottom = 16.dp)) {
            Text(
                text = "Devoluciones",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.blue_btn),
                modifier = Modifier
                    .weight(2f)
                    .align(Alignment.CenterVertically)
            )
            TextField(
                value = devoluciones.toString(),
                onValueChange = { devoluciones = it.toIntOrNull() ?: 0 },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(13.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = Color(0xFFe1e6ed)
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)

            )
        }
        Row(modifier = Modifier.padding(bottom = 16.dp, top = 16.dp)){
            Text(
                text = "Adjunta evidencia",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.blue_btn),
                modifier = Modifier
                    .weight(2f)
                    .align(Alignment.CenterVertically)
            )
            Button(
                onClick = { opIdDialog.value = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Filled.AddCircle,
                    contentDescription = "Tomar foto",
                    tint = Color(0xFF213E85)
                )
            }
        }
        Button(
            onClick = { onNavigateToHome() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF213E85)),
            shape = RoundedCornerShape(13.dp),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .height(65.dp)
                .padding(top = 16.dp)
        ) {
            Text(
                text = "Finalizar Ruta",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }

    if (opIdDialog.value) {
        TomarEvidencia(
            operationDialog = opIdDialog,
            onNavigateToGallery = { /* Aquí va la lógica al presionar el botón */ },
            onPhotoScreen = { onNavigateToPhoto() }
        )
    }
}

@Composable
fun ThirdScreen(onNavigateToHome: () -> Unit, onNavigateToPhoto: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf("Iniciar/Finalizar") }

    BackHandler {
        onNavigateToHome()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Banner()
            TitleText(title = "Operaciones Terceros")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 7.dp, end = 7.dp)
                ) {
                    Button(
                        onClick = { expanded = !expanded },
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
                            text = selectedItem,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF213E85)
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown, // Reemplaza con el icono adecuado
                            contentDescription = "Entrega a domicilio",
                            tint = Color(0xFF213E85)
                        )
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(
                            text = {
                                Text(text = "Iniciar ruta")
                            },
                            onClick = {
                                selectedItem = "Iniciar ruta"
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = {
                                Text(text = "Finalizar ruta")
                            },
                            onClick = {
                                selectedItem = "Finalizar ruta"
                                expanded = false
                            }
                        )
                    }
                }

                when (selectedItem) {
                    "Iniciar ruta" -> StartRoute(onNavigateToHome, onNavigateToPhoto)
                    "Iniciar/Finalizar" -> StartRoute(onNavigateToHome, onNavigateToPhoto)
                    "Finalizar ruta" -> EndRoute(onNavigateToHome, onNavigateToPhoto)
                    else -> Text(text = "Ocurrio un error. Reportalo a tu analizta")
                }
            }
        }
    }
}