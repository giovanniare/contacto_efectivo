package com.example.contacto_efectivo

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Path

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.logging.Level
import java.util.logging.Logger

interface OperationApiService {

    @PUT("operacion/{id}/codigo/")
    fun updateOperation(
        @Path("id") operationId: String,
        @Body operation: OperationApiResponse
    ): Call<Void>
}

object RetrofitClient {
    private const val BASE_URL = "http://137.184.226.78:8000/"

    val apiService: OperationApiService by lazy {

        Logger.getLogger(OkHttpClient::class.java.name).level = Level.FINE

        // Configura el interceptor de logging
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Puedes cambiar el nivel a BASIC, HEADERS, BODY
        }

        // Crea el cliente OkHttpClient con el interceptor
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OperationApiService::class.java)
    }
}

fun updateOperationStatus(operationId: String, operation: OperationApiResponse) {
    val apiService = RetrofitClient.apiService

    val call = apiService.updateOperation(operationId, operation)
    call.enqueue(object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            println("Esta es la url a la que mandamos request: ${call.request().url}")
            println("Este es el body que se mando: ${call.request().method}")
            println("Este es el body que se mando: ${call.request().headers}")
            println("Este es el body que se mando: ${call.request().body.toString()}")
            println("Este es el body que se mando: ${call.request().toString()}")
            if (response.isSuccessful) {
                println("Operación actualizada con éxito.")
            } else {
                println("Error en la actualización: ${response.code()}")
                println("Mensaje de error: ${response.message()}")
                println("Body de error: ${response.body()}")
                println("Full Error: ${response.raw()}")
                println("Cuerpo del error: ${response.errorBody()?.string()}")
            }
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            println("Error en la solicitud: ${t.message}")
        }
    })
}