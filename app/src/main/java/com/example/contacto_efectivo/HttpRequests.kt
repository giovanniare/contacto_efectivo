package com.example.contacto_efectivo

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

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
    suspend fun getFlow(endPointStr: String): FlowApiResponse? {
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
                        gson.fromJson(it, FlowApiResponse::class.java)
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
