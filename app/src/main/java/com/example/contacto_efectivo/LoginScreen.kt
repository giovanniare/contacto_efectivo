package com.example.contacto_efectivo

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.contacto_efectivo.ui.theme.Contacto_efectivoTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.system.exitProcess


@Composable
fun LogInScreen(onNavigateToHome: () -> Unit, viewModel: OperationsViewModel) {
    val context = LocalContext.current
    val image = painterResource(id = R.drawable.leon_png)
    var userName by remember { mutableStateOf<String>("") }
    var password by remember { mutableStateOf("") }
    var isLoading = mutableStateOf(true)

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
                value = userName.toString(),
                onValueChange = {newValue -> userName = newValue},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = TextFieldDefaults.colors(
                    unfocusedIndicatorColor = Color.Transparent,
                    unfocusedContainerColor = colorResource(id = R.color.gray_inputs),
                    unfocusedTextColor = colorResource(id = R.color.blue_btn)
                ),
                shape = RoundedCornerShape(13.dp),
                label = { Text(
                    text = "Ingresa tu numero de usuario",
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
                    unfocusedContainerColor = colorResource(id = R.color.gray_inputs),
                    unfocusedTextColor = colorResource(id = R.color.blue_btn)
                ),
                label = { Text(
                    text = "Ingresa tu contraseña",
                    color = colorResource(id = R.color.gray_ratiio),
                    fontSize = 14.sp) },
                visualTransformation = PasswordVisualTransformation(), // Aplica la transformación para ocultar el texto
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password // Configura el teclado para contraseñas
                ),
                modifier = Modifier
                    .background(colorResource(id = R.color.aaa))
                    .fillMaxWidth()
            )
            Button(
                onClick = {
                    accesoPermitido(
                        context = context,
                        user = userName,
                        password = password,
                        viewModel = viewModel,
                        navToHome = {onNavigateToHome()})
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

private fun accesoPermitido(context: Context, user: String, password: String, viewModel: OperationsViewModel, navToHome: () -> Unit) {
    val httpRequests = HttpRequests()
    val tokenManager = TokenManager(context)

    // Ejecutamos en una corutina, fuera del entorno Composable
    CoroutineScope(Dispatchers.IO).launch {
        println("User que se manda: $user")
        println("Password que se manda: $password")
        if (user == null || user == "" || password == null || password == "") {
            Toast.makeText(context, "Ingresa tus credenciales", Toast.LENGTH_SHORT).show()
        } else {
            val apiResponse = httpRequests.auth(user = user, pass = password)

            withContext(Dispatchers.Main) {
                if (apiResponse != null) {
                    viewModel.repartidorId.value = apiResponse.user_id.toInt()
                    tokenManager.saveToken(apiResponse)

                    val userData = httpRequests.getUser(apiResponse.user_id, apiResponse.token)
                    if (userData != null) {
                        val userManager = UserManager(context)
                        userManager.saveUser(userData)
                    }

                    navToHome()

                } else {
                    Toast.makeText(context, "Ingresa credenciales validas", Toast.LENGTH_SHORT).show()
                    viewModel.repartidorId.value = null
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginPreview() {
    Contacto_efectivoTheme {
        LogInScreen(onNavigateToHome = {}, viewModel = viewModel())
    }
}