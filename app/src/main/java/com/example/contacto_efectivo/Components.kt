package com.example.contacto_efectivo

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController


@Composable
fun AccountMenu(navController: NavController, viewModel: OperationsViewModel) {
    var expanded by remember { mutableStateOf(false) }
    val logout = remember { mutableStateOf(false) }
    val userManager = UserManager(LocalContext.current)
    val tokenManager = TokenManager(LocalContext.current)
    val httpRequests = HttpRequests()

    if (logout.value) {
        LaunchedEffect(logout.value) {
            val logoutResponse = httpRequests.logOut(tokenManager.getToken())
            if (logoutResponse != null) {
                viewModel.tipoOperacion.value = null
                viewModel.repartidorId.value = null
                viewModel.getData.value = true
                viewModel.operationsList.value = emptyList()
                viewModel.municipios.value = emptyList()
                tokenManager.clearToken()
                userManager.clearUser()
                navController.navigate("login")
            }
            logout.value = false
        }
    }


    Button(
        onClick = {
            viewModel.tipoOperacion.value = null
            expanded = !expanded
        },
        shape = RoundedCornerShape(13.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        modifier = Modifier
            .fillMaxHeight()
            .wrapContentSize(unbounded = true)
    ) {
        Icon(
            imageVector = Icons.Default.AccountBox,
            contentDescription = "Cuenta",
            tint = Color.White
        )
    }
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        DropdownMenuItem(
            text = {
                Text(text = stringResource(id = R.string.opt_logout))
            },
            onClick = { logout.value = true }
        )
    }
}

@Composable
fun Banner(navController: NavController, viewModel: OperationsViewModel) {
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
        Column {
            Text(
                text = "Contacto Efectivo",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.inter_extrabold)),
                color = Color.White,
                modifier = Modifier.padding(start = 15.dp, top = 23.dp)
            )
            Box(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight()
                    .align(alignment = Alignment.End)
            ) {
                AccountMenu(navController, viewModel)
            }
        }

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
    viewModel: OperationsViewModel,
    onPhotoScreen: () -> Unit)
{

    if (operationDialog.value) {
        val context = LocalContext.current
        var imageUri by remember { mutableStateOf<Uri?>(null) }
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let {
                val imagePath = getRealPathFromURI(context, it)
                viewModel.imageName.value = imagePath
                imageUri = uri
                uploadImage(context, imagePath, imageUri, viewModel) // Sube la imagen seleccionada
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
                // TODO
            },
            dismissButton = {
                Button(onClick = { operationDialog.value = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

}