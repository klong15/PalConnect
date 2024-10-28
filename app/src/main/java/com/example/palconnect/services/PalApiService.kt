package com.example.palconnect.services

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.basicAuth
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode

class PalApiService {

    private var _client = HttpClient(CIO)
    private var _ip: String = ""
    private var _password: String = ""

    suspend fun getServerInfo(error: suspend (HttpResponse) -> Unit = {},
                              exception: suspend (Exception) -> Unit = { e -> },
                              success: suspend (HttpResponse) -> Unit,) {
        try {
            val response = getServerInfo()

            if (response.status == HttpStatusCode.OK) {
                success(response)
            } else {
                error(response)
            }
        } catch (e: Exception) {
            exception(e)
        }
    }

    suspend fun getServerInfo(): HttpResponse {
        return makeGetRequest("/v1/api/info")
    }

    suspend fun getServerMetrics(error: suspend (HttpResponse) -> Unit = {},
                              exception: suspend (Exception) -> Unit = {},
                              success: suspend (HttpResponse) -> Unit,) {
        try {
            val response = getServerMetrics()

            if (response.status == HttpStatusCode.OK) {
                println("Metrics success!")
                success(response)
            } else {
                println("Metrics error!")
                error(response)
            }
        } catch (e: Exception) {
            println("Metrics exception!")
            exception(e)
        }
    }

    suspend fun getServerMetrics(): HttpResponse {
        return makeGetRequest("/v1/api/metrics")
    }

    private suspend fun makeGetRequest(endpoint: String): HttpResponse {
        val request: HttpResponse = _client.request("http://${_ip}${endpoint}") {
            method = HttpMethod.Get
            headers {
                basicAuth("admin", _password)
            }
        }

        return request
    }

    suspend fun announceMessage(
        message: String,
        error: suspend (HttpResponse) -> Unit = {},
        exception: suspend (Exception) -> Unit = {},
        success: suspend (HttpResponse) -> Unit = {},
    ) {
        try {
            val response = announceMessage(message)

            if (response.status == HttpStatusCode.OK) {
                success(response)
            } else {
                error(response)
            }
        } catch (e: Exception) {
            exception(e)
        }
    }

    suspend fun announceMessage(message: String): HttpResponse {
        val jsonMessage = "{\n  \"message\": \"$message\"\n}"
        return makePostRequest("/v1/api/announce", jsonMessage)
    }

    private suspend fun makePostRequest(endpoint: String, body: String): HttpResponse {
        val request: HttpResponse = _client.request("http://${_ip}${endpoint}") {
            method = HttpMethod.Post
            headers {
                basicAuth("admin", _password)
            }
            setBody(body)
        }

        return request
    }

    fun setServerInfo(ip: String, password: String) {
        _ip = ip;
        _password = password
    }
}

