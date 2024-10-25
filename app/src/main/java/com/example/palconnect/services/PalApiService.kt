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

interface PalApiService {
    @GET("api/info")
    fun getServerInfo() : String
}

class PalPalApiServiceImpl: PalApiService {

    override fun getServerInfo() : String {
        return ""
    }
}

class PalApiModule(
    private val appContext: Context
) {
    val palApiService: PalApiService by lazy {
        Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(PalApiService::class.java)
    }
}

