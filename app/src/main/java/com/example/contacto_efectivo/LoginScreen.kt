package com.example.contacto_efectivo

import android.annotation.SuppressLint
import android.app.AlertDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.contacto_efectivo.ui.theme.Contacto_efectivoTheme


@Composable
fun LogInScreen(onNavigateToHome: () -> Unit) {
    val image = painterResource(id = R.drawable.leon_png)
    var userName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var userLabel = ""
    var passLabel = ""

    userLabel = if (userName == "") {
        "Ingresa tu numbre de usuario"
    } else {
        userName
    }

    passLabel = if (password == "" ) {
        "Ingresa tu contraseña"
    } else {
        password
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxHeight()
            .background(colorResource(id = R.color.blue_login))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp)
            ) {
                Image(
                    painter = image,
                    contentDescription = stringResource(id = R.string.logo_desc),
                )
            }

            Text(
                text = stringResource(id = R.string.user_str),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.White,
                modifier = Modifier.padding(top=15.dp, bottom = 10.dp)
            )
            TextField(
                value = userName,
                onValueChange = {newText -> userName = newText},
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = colorResource(id = R.color.gray_inputs)
                ),
                shape = RoundedCornerShape(13.dp),
                label = { Text(
                    text = userLabel,
                    color = colorResource(id = R.color.gray_ratiio),
                    fontSize = 14.sp)
                },
                modifier = Modifier
                    .fillMaxWidth()
            )
            Text(
                text = stringResource(id = R.string.pass_str),
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 20.sp,
                modifier = Modifier.padding(top=15.dp, bottom = 10.dp)
            )
            TextField(
                value = password,
                onValueChange = {newText -> password = newText },
                shape = RoundedCornerShape(13.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = colorResource(id = R.color.gray_inputs)
                ),
                label = { Text(
                    text = passLabel,
                    color = colorResource(id = R.color.gray_ratiio),
                    fontSize = 14.sp) },
                modifier = Modifier
                    .background(colorResource(id = R.color.aaa))
                    .fillMaxWidth()
            )
            Button(
                onClick = {
                    onNavigateToHome()
                },
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.gold_theme)),
                shape = RoundedCornerShape(13.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.log_btn),
                    fontSize = 20.sp,
                    color = Color.White
                )
            }
        }

    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginPreview() {
    Contacto_efectivoTheme {
        LogInScreen(onNavigateToHome = {})
    }
}