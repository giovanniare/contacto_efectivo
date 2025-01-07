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
    private val urlApiBase_ = "https://walrus-app-ja4xp.ondigitalocean.app"
    private val client = OkHttpClient()
    private val gson = Gson() // Inicializa Gson

    suspend fun getOperation(endPointStr: String): OperationApiResponse? {
        println("Esta es la url que se manda: $urlApiBase_/$endPointStr")
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("$urlApiBase_/$endPointStr")
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
        println("Esta es la url que se manda: $urlApiBase_/$endPointStr")
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("$urlApiBase_/$endPointStr")
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

    suspend fun getUser(endPointStr: String): User? {
        println("Esta es la url que se manda: $urlApiBase_/$endPointStr")
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("$urlApiBase_/$endPointStr")
                .build()

            try {
                val response: Response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    responseBody?.let {
                        // Parsear el JSON a ApiResponse
                        gson.fromJson(it, User::class.java)
                    }
                } else {
                    println("Error: ${response.code}")
                    null
                }
            } catch (e: Exception) {
                println("Exception: ${e.message}")
                println("Exception: ${e.localizedMessage}")
                println("Exception: ${e.stackTrace}")
                println("Exception: ${e.toString()}")
                null
            }
        }
    }

    suspend fun auth(user: String, pass: String): AuthData? {
        println("Esta es la url que se manda: $urlApiBase_/auth/log_in/")
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("$urlApiBase_/auth/log_in/")
                .addHeader("user", user)
                .addHeader("pass", pass)
                .build()

            try {
                val response: Response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    responseBody?.let {
                        // Parsear el JSON a ApiResponse
                        gson.fromJson(it, AuthData::class.java)
                    }
                } else {
                    println("Error: ${response.code}")
                    null
                }
            } catch (e: Exception) {
                println("Exception: ${e.message}")
                println("Exception: ${e.localizedMessage}")
                println("Exception: ${e.stackTrace}")
                println("Exception: ${e.toString()}")
                null
            }
        }
    }

    suspend fun validateToken(token: String?): AuthData? {
        if (token == null || token == "") {
            return null
        }

        println("Esta es la url que se manda: $urlApiBase_/auth/verify/")
        return withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url("$urlApiBase_/auth/verify/")
                .addHeader("token", token)
                .build()

            try {
                val response: Response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    responseBody?.let {
                        // Parsear el JSON a ApiResponse
                        gson.fromJson(it, AuthData::class.java)
                    }
                } else {
                    println("Error: ${response.code}")
                    null
                }
            } catch (e: Exception) {
                println("Exception: ${e.message}")
                println("Exception: ${e.localizedMessage}")
                println("Exception: ${e.stackTrace}")
                println("Exception: ${e.toString()}")
                null
            }
        }
    }
}
