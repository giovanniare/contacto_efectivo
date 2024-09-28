package com.example.contacto_efectivo

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ConsultScreen(onNavigateToHome: () -> Unit) {

    BackHandler {
        onNavigateToHome()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Banner()
        TitleText(title = "Consulta de estatus")
        Column(
            modifier = Modifier
                .padding(25.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "En ruta",
                fontSize = 30.sp,
                color = Color(0xFF213E85),
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.inter_extrabold)),
                modifier = Modifier
                    .padding(top = 30.dp)
                    .align(Alignment.CenterHorizontally)

            )
            Text(
                text = "Informacion general del paquete",
                fontSize = 25.sp,
                textAlign = TextAlign.Center,
                color = Color(0xFF213E85),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 30.dp, bottom = 30.dp)
                    .align(Alignment.CenterHorizontally)

            )
            Text(
                text = "Direccion:",
                fontSize = 18.sp,
                textAlign = TextAlign.Justify,
                color = Color(0xFF213E85),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(start = 27.dp)

            )
            Text(
                text = "Remitente:",
                fontSize = 18.sp,
                textAlign = TextAlign.Justify,
                color = Color(0xFF213E85),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(start = 27.dp)
            )
            Text(
                text = "Destinatario:",
                fontSize = 18.sp,
                textAlign = TextAlign.Justify,
                color = Color(0xFF213E85),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(start = 27.dp)
            )
        }

    }
}