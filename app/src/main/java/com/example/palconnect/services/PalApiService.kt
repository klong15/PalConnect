package com.example.palconnect.services

import android.content.Context
import android.net.Credentials
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.basicAuth
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.cio.Request

private const val BASE_URL =
    "https://android-kotlin-fun-mars-server.appspot.com"
//private const val BASE_URL = "http://192.168.0.1:8212/"
//private val retrofit = Retrofit.Builder()
//    .addConverterFactory(ScalarsConverterFactory.create())
//    .baseUrl(BASE_URL)
//    .build()

//interface PalRetroService {
//    @GET("api/info")
//    suspend fun getServerInfo() : String
//
//    @GET("photos")
//    suspend fun getPhotos(): String
//}

class PalApiService {

    private var _client = HttpClient(CIO)
    private var _ip: String = ""
    private var _password: String = ""

    suspend fun getServerInfo(): HttpResponse {
        return makeRequest("/v1/api/info")
    }

    private suspend fun makeRequest(endpoint: String): HttpResponse {
        val request: HttpResponse = _client.request("http://${_ip}${endpoint}") {
            method = HttpMethod.Get
            headers {
                append(HttpHeaders.Accept, "application/json")
                basicAuth("admin", _password)
            }
        }

        return request
    }

    fun setServerInfo(ip: String, password: String) {
        _ip = ip;
        _password = password
    }
}

class PalApiModule(
    private val appContext: Context
) {
//    val palRetroService: PalRetroService by lazy {
//        Retrofit.Builder()
//            .addConverterFactory(ScalarsConverterFactory.create())
//            .baseUrl(BASE_URL)
//            .build()
//            .create(PalRetroService::class.java)
//    }

    val palApiService: PalApiService by lazy {
        PalApiService()
    }
}

