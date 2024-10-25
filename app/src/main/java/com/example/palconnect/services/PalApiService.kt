package com.example.palconnect.services

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

private const val BASE_URL =
    "https://android-kotlin-fun-mars-server.appspot.com"
//private const val BASE_URL = "http://192.168.0.1:8212/"
private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface PalRetroService {
    @GET("api/info")
    suspend fun getServerInfo() : String

    @GET("photos")
    suspend fun getPhotos(): String
}

class PalApiService {

    private val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()
    var service: PalRetroService? = null

    suspend fun getPhotos(): String {
        return service!!.getPhotos()
    }

    fun buildRetroService(ip: String) {

        service =  Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(httpClient.build())
            .build()
            .create(PalRetroService::class.java)

    }

}

class PalApiModule(
    private val appContext: Context
) {
    val palRetroService: PalRetroService by lazy {
        Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(PalRetroService::class.java)
    }

    val palApiService: PalApiService by lazy {
        PalApiService()
    }
}

