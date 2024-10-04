package com.example.contacto_efectivo

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.lang.reflect.Type

class HttpRequests {
    private val urlApiBase_ = "http://137.184.226.78:8000/"
    private val client = OkHttpClient()
    private val gson = Gson() // Inicializa Gson

    suspend fun getOperation(endPointStr: String): OperationApiResponse? {
        println("Esta es la url que se manda: $urlApiBase_$endPointStr")
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("$urlApiBase_$endPointStr")
                .build()

            try {
                val response: Response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    responseBody?.let {
                        // Parsear el JSON a ApiResponse
                        gson.fromJson(it, OperationApiResponse::class.java)
                    }
                } else {
                    println("Error: ${response.code}")
                    null
                }
            } catch (e: Exception) {
                println("Exception: ${e.message}")
                null
            }
        }
    }

    suspend fun getAllRepartidor(endPointStr: String): List<OperationApiResponse>? {
        println("Esta es la url que se manda: $urlApiBase_$endPointStr")
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("$urlApiBase_$endPointStr")
                .build()

            try {
                val response: Response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    responseBody?.let {
                        // Parsear el JSON a una lista de ApiResponse
                        val listType: Type = object : TypeToken<List<OperationApiResponse>>() {}.type
                        return@withContext gson.fromJson<List<OperationApiResponse>>(it, listType)
                    }
                } else {
                    println("Error: ${response.code}")
                    null
                }
            } catch (e: Exception) {
                println("Exception: ${e.message}")
                null
            }
        }
    }
}
