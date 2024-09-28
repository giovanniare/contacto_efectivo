package com.example.contacto_efectivo

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun UpdateScreen(onNavigateToHome: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var comments by remember { mutableStateOf("") }
    var selectedItem by remember { mutableStateOf("Estatus") }

    BackHandler {
        onNavigateToHome()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Banner()
        TitleText(title = "Actualizacion de estatus")
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Selecciona el nuevo estatus de la operacion",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF213E85)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 7.dp, bottom = 7.dp)
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
                        fontFamily = FontFamily(Font(R.font.inter_extrabold)),
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
                            Text(text = "En ruta")
                        },
                        onClick = {
                            selectedItem = "En ruta"
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(text = "Recoleccion")
                        },
                        onClick = {
                            selectedItem = "Recoleccion"
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(text = "Entregado")
                        },
                        onClick = {
                            selectedItem = "Entregado"
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(text = "Reprogramar")
                        },
                        onClick = {
                            selectedItem = "Reprogramar"
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(text = "Recolectado")
                        },
                        onClick = {
                            selectedItem = "Recolectado"
                            expanded = false
                        }
                    )
                }
            }
            Text(
                text = "Comentarios Adicionales",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF213E85),
                modifier = Modifier
                    .padding(top = 26.dp)
            )
            TextField(
                value = comments,
                onValueChange = {newText -> comments = newText},
                label = {
                    Text(
                        text = "Escribe aqui cualquier incidencia",
                        color = Color(0xFF888888),
                        fontSize = 14.sp
                    )
                },
                maxLines = Int.MAX_VALUE,
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = Color(0xFFe1e6ed)
                ),
                shape = RoundedCornerShape(13.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .padding(top = 7.dp, bottom = 7.dp)
            )
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(top = 23.dp, start = 28.dp, end = 28.dp)
                    .fillMaxWidth()
            ) {
                Button(
                    onClick = { onNavigateToHome() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(13.dp),
                    border = BorderStroke(1.dp, Color(0xFFE8D67E)),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .padding(end = 8.dp)
                ) {
                    Text(
                        text = "Mover",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE8D67E),
                    )
                }
                Button(
                    onClick = { onNavigateToHome() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF213E85)),
                    shape = RoundedCornerShape(13.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .padding(start = 8.dp)
                ) {
                    Text(
                        text = "Cancelar",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

            }
        }
    }
}