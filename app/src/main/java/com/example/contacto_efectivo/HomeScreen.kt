package com.example.contacto_efectivo

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.contacto_efectivo.ui.theme.Contacto_efectivoTheme
import com.google.gson.Gson

@Composable
fun HomeScreen(navController: NavController, deliveryMan: String) {
    val showDialog = remember { mutableStateOf(false) }
    val opIdDialog = remember { mutableStateOf(false) }
    val optMenu = remember { mutableStateOf("") }
    val activity = (LocalContext.current as? Activity)
    
    BackHandler {
        showDialog.value = true
    }
    
    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
    ) {
        Banner()
        TitleText(title = "Bienvenido $deliveryMan")
        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 16.dp)
                    .height(60.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    OperationsMenu(navController)
                }

                Button(
                    onClick = { /*TODO*/ },
                    shape = RoundedCornerShape(13.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .wrapContentSize(unbounded = true)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Ubicación",
                        tint = Color(0xFF213E85)
                    )
                    Text(
                        text = stringResource(id = R.string.menu_rta),
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily(Font(R.font.inter_extrabold)),
                        color = Color(0xFF213E85)
                    )
                }
                Button(
                    onClick = { navController.navigate("consult_screen") },
                    shape = RoundedCornerShape(13.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .wrapContentSize(unbounded = true)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Consulta Estatus",
                        tint = Color(0xFF213E85)
                    )
                    Text(
                        text = stringResource(id = R.string.menu_sts),
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily(Font(R.font.inter_extrabold)),
                        color = Color(0xFF213E85)
                    )
                }
            }
        }

    }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false},
            title = { Text(text = "¿Salir de la aplicación?") },
            text = { Text("¿Estás seguro que quieres salir?") },
            confirmButton = {
                Button(onClick = { activity?.finish() }) {
                    Text("Sí")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog.value = false }) {
                    Text("No")
                }
            }
        )
    }


}

@Composable
fun OperationsMenu(navController: NavController){
    var expanded by remember { mutableStateOf(false) }

    Button(
        onClick = { expanded = !expanded },
        shape = RoundedCornerShape(13.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        modifier = Modifier
            .fillMaxHeight()
            .wrapContentSize(unbounded = true)
    ) {
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "Entrega a domicilio",
            tint = Color(0xFF213E85)
        )
        Text(
            text = stringResource(id = R.string.menu_opt),
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily(Font(R.font.inter_extrabold)),
            color = Color(0xFF213E85)
        )
    }
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        DropdownMenuItem(
            text = {
                Text(text = stringResource(id = R.string.opt_terceros))
            },
            onClick = { navController.navigate("third_screen") }
        )
        DropdownMenuItem(
            text = {
                Text(text = stringResource(id = R.string.opt_prod))
            },
            onClick = { navController.navigate("update_screen") }
        )
        DropdownMenuItem(
            text = {
                Text(text = stringResource(id = R.string.opt_clt))
            },
            onClick = { navController.navigate("update_screen") }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomePreview() {
    Contacto_efectivoTheme {
        HomeScreen(
            navController = rememberNavController(),
            deliveryMan = stringResource(id = R.string.delivery_man)
        )
    }
}