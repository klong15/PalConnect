package com.example.palconnect.services

import android.content.Context
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "http://192.168.0.1:8212/"
private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface ApiService {
    @GET("api/info")
    fun getServerInfo() : String
}

class PalApiService: ApiService {

    override fun getServerInfo() : String {
        return ""
    }
}

class ApiModule(
    private val appContext: Context
) {
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(ApiService::class.java)
    }
}

