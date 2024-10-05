package com.example.contacto_efectivo

import android.app.Activity
import android.content.Context
import android.media.session.MediaSession.Token
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.contacto_efectivo.ui.theme.Contacto_efectivoTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Composable
fun HomeScreen(navController: NavController, deliveryMan: String, viewModel: OperationsViewModel) {
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
            ScrollableList(viewModel = viewModel)
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
                Button(onClick = {
                    viewModel.repartidorId.value = null
                    activity?.finish() }) {
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

@Composable
fun ScrollableList(viewModel: OperationsViewModel) {
    // Usa 'remember' para mantener la lista entre recomposiciones
    val context = LocalContext.current
    val operations = remember { mutableStateOf(listOf<OperationApiResponse>()) }
    val getData = remember { mutableStateOf(true) }  // Seteamos en true para que dispare la llamada al API
    val httpRequests = HttpRequests()

    if (getData.value) {
        LaunchedEffect(Unit) {
            val apiResponse = httpRequests.getAllRepartidor("operacion/${viewModel.repartidorId.value}/repartidor/")
            getData.value = false
            apiResponse?.let {
                // Actualiza la lista con los resultados de la API
                operations.value = it
            }
        }
    }

    val filteredOperations = operations.value.filter { item -> item.status !in viewModel.noMoreActions }
    val opActivas = filteredOperations.size

    Column {
        Text(
            text = "Operaciones asignadas: ${opActivas ?: 0}",
            textAlign = TextAlign.Justify,
            color = Color(0xFF213E85),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()  // Ocupar todo el ancho
                .fillMaxHeight(0.7f)  // Limitar a 50% de la altura de la pantalla
                .padding(16.dp)
        ) {
            items(operations.value.filter { item ->
                item.status !in viewModel.noMoreActions
            }) { item ->
                ListItem(item = item)
                Divider() // Divider entre los items
            }
        }
        Button(
            onClick = {
                getData.value = true
                actualizarLista(context)},
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF213E85)),
            shape = RoundedCornerShape(13.dp),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .height(65.dp)
                .padding(top = 16.dp)
        ) {
            Text(
                text = "Actualizar",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }

}

@Composable
fun ListItem(item: OperationApiResponse) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {
        Text(
            text = "Código: ${item.codigo ?: "Sin código"}",
            textAlign = TextAlign.Justify,
            color = Color(0xFF213E85),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        )
        Text(
            text = "Tipo de Operación: ${item.id_tipo_operacion}",
            textAlign = TextAlign.Justify,
            color = Color(0xFF213E85),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        )
    }
}

private fun actualizarLista(context: Context) {
    Toast.makeText(context, "Actualizacion Exitosa!", Toast.LENGTH_SHORT).show()
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomePreview() {
    Contacto_efectivoTheme {
        HomeScreen(
            navController = rememberNavController(),
            deliveryMan = stringResource(id = R.string.delivery_man),
            viewModel = OperationsViewModel()
        )
    }
}