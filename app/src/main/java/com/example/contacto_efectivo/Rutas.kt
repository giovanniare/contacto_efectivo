package com.example.contacto_efectivo

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.location.FusedLocationProviderClient
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

/*
fun buildGoogleMapsDirectionsUrl(
    operaciones: List<OperationApiResponse>,
    apiKey: String
): String? {
    // Filtrar direcciones válidas (no nulas ni vacías)
    val direcciones = operaciones.mapNotNull { it.direccion_final }.filter { it.isNotBlank() }
    if (direcciones.size < 2) return null // Necesitamos al menos origen y destino

    val origin = direcciones.first()
    val destination = direcciones.last()
    val waypoints = if (direcciones.size > 2) {
        direcciones.subList(1, direcciones.size - 1).joinToString("|")
    } else {
        ""
    }

    val baseUrl = "https://maps.googleapis.com/maps/api/directions/json"
    return "$baseUrl?origin=$origin&destination=$destination&waypoints=$waypoints&key=$apiKey"
}

suspend fun getDirectionsFromGoogleMaps(
    operaciones: List<OperationApiResponse>,
    apiKey: String
): String? = withContext(Dispatchers.IO) {
    val url = buildGoogleMapsDirectionsUrl(operaciones, apiKey) ?: return@withContext null
    val client = OkHttpClient()

    val request = Request.Builder()
        .url(url)
        .build()

    try {
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            response.body?.string()
        } else {
            println("Error en Google Maps API: ${response.code}")
            null
        }
    } catch (e: Exception) {
        println("Excepción al llamar Google Maps API: ${e.message}")
        null
    }
}
*/

class RouteManager(context: Context) {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val context = context

    fun initializeLocationClient() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }

    fun limpiarDireccion(direccion: String): String {
        val regex = Regex("Col:[^,]*")
        return direccion
            // Quitar "Calle." al inicio
            .replace("Calle.", "", ignoreCase = true)
            .replace("Av.", "", ignoreCase = true)
            // Quitar "Int null"
            .replace("Int null", "", ignoreCase = true)
            // Quitar #
            .replace("#", "")
            // Quitar dobles comas o espacios innecesarios
            .replace(", ", " ")
            .replace(",", "")
            .replace(regex, "")
            .replace("\\s+".toRegex(), " ") // reemplaza espacios múltiples por uno
            .trim()
    }

    fun openDirection(operation: OperationApiResponse, viewModel: OperationsViewModel) {
        if (!::fusedLocationClient.isInitialized) {
            return
        }

        val LOCATION_PERMISSION_REQUEST = 1001
        val context = context

        if (operation.direccion_final == null || operation.codigo_postal == null) {
            Toast.makeText(context, "Dirección inválida", Toast.LENGTH_SHORT).show()
            return
        }

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val lat = location.latitude
                    val lon = location.longitude
                    Toast.makeText(context, "Lat: $lat, Lon: $lon", Toast.LENGTH_LONG).show()
                    val origin = "$lat,$lon"

                    val dir = operation.direccion_final
                    val cp = operation.codigo_postal
                    val munId  = operation.monicipioId
                    val municipios = viewModel.municipios.value

                    val direccion = "$dir, $cp, ${municipios.find { it.id == munId }?.nombre}"
                    val direccionValida = limpiarDireccion(direccion)

                    val url = buildString {
                        append("https://www.google.com/maps/dir/?api=1")
                        append("&origin=$origin")
                        append("&destination=$direccionValida")
                    }
                    println("URL: $url")

                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                        setPackage("com.google.android.apps.maps")
                    }

                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Google Maps no está instalado", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(context, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show()
                }
            }

    }

    fun openRouteInGoogleMaps(operations: List<OperationApiResponse>, viewModel: OperationsViewModel) {

        if (!::fusedLocationClient.isInitialized) {
            return
        }
        val LOCATION_PERMISSION_REQUEST = 1001
        val context = context

        if (operations.isEmpty()) {
            Toast.makeText(context, "No hay direcciones disponibles", Toast.LENGTH_SHORT).show()
            return
        }

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val lat = location.latitude
                    val lon = location.longitude
                    Toast.makeText(context, "Lat: $lat, Lon: $lon", Toast.LENGTH_LONG).show()
                    val origin = "$lat,$lon"

                    val addresses = operations.mapNotNull { op ->
                        if (op.status in viewModel.noMoreActions) {
                            return@mapNotNull null
                        }

                        val dir = op.direccion_final?.takeIf { it.isNotBlank() }
                        val cp = op.codigo_postal?.takeIf { true }
                        val munId  = op.monicipioId?.takeIf { true }
                        val municipios = viewModel.municipios.value
                        if (dir != null && cp != null && munId != null) {
                            println("Operacion: $op")
                            println("$dir, $cp, ${municipios.find { it.id == munId }?.nombre}")
                            "$dir, $cp, ${municipios.find { it.id == munId }?.nombre}"
                        } else {
                            dir // si no hay CP, solo devuelve la dirección
                        }
                    }

                    if (addresses.size < 2) {
                        Toast.makeText(context, "Se necesitan al menos origen y destino", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }

                    val direccionesLimpias = addresses.map { limpiarDireccion(it) }
                    direccionesLimpias.map { println("Dirección: $it") }
                    val destination = direccionesLimpias.last().replace(" ", "+")
                    val waypoints = direccionesLimpias.dropLast(1) // todos los intermedios

                    val waypointsString = waypoints.joinToString("|") { it.replace(" ", "+") }

                    val url = buildString {
                        append("https://www.google.com/maps/dir/?api=1")
                        append("&origin=$origin")
                        append("&destination=$destination")
                        if (waypoints.isNotEmpty()) append("&waypoints=$waypointsString")
                        append("&travelmode=driving")
                    }
                    println("URL: $url")

                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                        setPackage("com.google.android.apps.maps")
                    }

                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Google Maps no está instalado", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT)
                        .show()
                }
            }


        // Extraer todas las direcciones_final que no sean null o vacías
        /*
        val addresses = operations.mapNotNull { it.direccion_final?.takeIf { dir -> dir.isNotBlank() } }

        if (addresses.size < 2) {
            Toast.makeText(context, "Se necesitan al menos origen y destino", Toast.LENGTH_SHORT).show()
            return
        }

        val destination = addresses.last().replace(" ", "+")
        val waypoints = addresses.drop(1).dropLast(1) // todos los intermedios

        val waypointsString = waypoints.joinToString("|") { it.replace(" ", "+") }

        val url = buildString {
            append("https://www.google.com/maps/dir/?api=1")
            append("&origin=$origin")
            append("&destination=$destination")
            if (waypoints.isNotEmpty()) append("&waypoints=$waypointsString")
            append("&travelmode=driving")
        }

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            setPackage("com.google.android.apps.maps")
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Google Maps no está instalado", Toast.LENGTH_SHORT).show()
        }

         */
    }
}

