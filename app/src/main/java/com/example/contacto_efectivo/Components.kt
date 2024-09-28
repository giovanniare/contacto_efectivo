package com.example.contacto_efectivo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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