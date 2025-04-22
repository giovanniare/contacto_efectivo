package com.example.contacto_efectivo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.session.MediaSession.Token
import android.net.Uri
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
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
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
import kotlinx.coroutines.processNextEventInCurrentThread

@Composable
fun HomeScreen(navController: NavController, viewModel: OperationsViewModel) {
    val showDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val activity = (LocalContext.current as? Activity)
    val userManager = UserManager(LocalContext.current)
    val userName = userManager.getName()
    var deliveryMan by remember { mutableStateOf("") }

    if (userName != null) {
        deliveryMan = userName
    }
    
    BackHandler {
        showDialog.value = true
        viewModel.tipoOperacion.value = null
    }
    
    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
    ) {
        Banner(navController, viewModel)
        TitleText(title = "Bienvenido $deliveryMan")
        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            ScrollableList(viewModel = viewModel, navController = navController)
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
                    OperationsMenu(navController, viewModel)
                }

                Button(
                    onClick = {
                        val origin = "CDMX"
                        val destination = "Guadalajara"
                        val waypoints = listOf("Puebla", "Querétaro")
                        val waypointsString = waypoints.joinToString("|") { it.replace(" ", "+") }
                        val url = "https://www.google.com/maps/dir/?api=1&origin=$origin&destination=$destination&waypoints=$waypointsString&travelmode=driving"

                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        intent.setPackage("com.google.android.apps.maps")
                        context.startActivity(intent)
                    },
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
fun OperationsMenu(navController: NavController, viewModel: OperationsViewModel){
    var expanded by remember { mutableStateOf(false) }

    Button(
        onClick = {
            viewModel.tipoOperacion.value = null
            expanded = !expanded },
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
            onClick = {
                viewModel.tipoOperacion.value = "terceros"
                navController.navigate("third_screen") }
        )
        DropdownMenuItem(
            text = {
                Text(text = stringResource(id = R.string.opt_prod))
            },
            onClick = {
                viewModel.tipoOperacion.value = "producto"
                navController.navigate("update_screen") }
        )
        DropdownMenuItem(
            text = {
                Text(text = stringResource(id = R.string.opt_clt))
            },
            onClick = {
                viewModel.tipoOperacion.value = "interna"
                navController.navigate("update_screen") }
        )
    }
}

@Composable
fun ScrollableList(viewModel: OperationsViewModel, navController: NavController) {
    // Usa 'remember' para mantener la lista entre recomposiciones
    val context = LocalContext.current
    val operations = remember { mutableStateOf(listOf<OperationApiResponse>()) }
    val getData = viewModel.getData
    val httpRequests = HttpRequests()
    val tokenManager = TokenManager(context)

    if (getData.value) {
        LaunchedEffect(Unit) {
            viewModel.operationSelected = null
            viewModel.dataFromSelectedItem.value = null
            viewModel.repartidorId.value = null
            viewModel.tipoOperacion.value = null

            val apiResponse = httpRequests.getAllRepartidor("operacion/repartidor/", tokenManager.getToken(), tokenManager.getUserId())
            apiResponse?.let {
                // Actualiza la lista con los resultados de la API
                operations.value = it
                getData.value = false
            }

        }

    }

    val filteredOperations = operations.value.filter { item -> item.status !in viewModel.noMoreActions }
    val opActivas = filteredOperations.size
    val operationMap = mutableMapOf<String?, OperationApiResponse>()

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
                operationMap[item.codigo] = item
                ListItem(item = item, operationMap = operationMap, viewModel = viewModel, navController = navController)
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
                text = "Sincronizar",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }

}

@Composable
fun ListItem(item: OperationApiResponse, operationMap: MutableMap<String?, OperationApiResponse>, viewModel: OperationsViewModel, navController: NavController) {
    val codigo = item.codigo

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {
        Text(
            text = "Código: ${codigo ?: "Sin código"} \n" +
                "Tipo de Operación: ${item.id_tipo_operacion}",
            textAlign = TextAlign.Justify,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF213E85),
            fontSize = 14.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        )
        Row(
            horizontalArrangement = Arrangement.Absolute.Left,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 7.dp, bottom = 7.dp)
                .height(40.dp)
        ) {
            Button(
                onClick = {
                    println("Codigo: $codigo")
                    viewModel.operationSelected = operationMap[codigo]
                    viewModel.dataFromSelectedItem.value = true
                    println("operacion: ${viewModel.operationSelected}")
                    navController.navigate("consult_screen")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF213E85)),
                shape = RoundedCornerShape(13.dp),
                modifier = Modifier
                    .height(40.dp)
                    .padding(end = 5.dp)
            ) {
                Text(
                    text = "Info",
                    color = Color.White
                )
            }
            if (operationMap[codigo]?.id_tipo_operacion == "terceros") {
                Button(
                    onClick = {
                        println("Codigo: $codigo")
                        viewModel.operationSelected = operationMap[codigo]
                        viewModel.dataFromSelectedItem.value = true
                        viewModel.repartidorId.value = operationMap[codigo]?.repartidor
                        viewModel.tipoOperacion.value = operationMap[codigo]?.id_tipo_operacion
                        println("operacion: ${viewModel.operationSelected}")
                        val screen = if (item.id_tipo_operacion == "terceros") "third_screen" else "update_screen"
                        navController.navigate(screen) {
                            popUpTo("home_screen") { inclusive = true }
                            popUpTo(screen) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF213E85)),
                    shape = RoundedCornerShape(13.dp),
                    modifier = Modifier
                        .height(40.dp)
                        .padding(start = 5.dp)
                ) {
                    Text(
                        text = "Actualizar",
                        color = Color.White
                    )
                }
            }

        }
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
            viewModel = OperationsViewModel()
        )
    }
}