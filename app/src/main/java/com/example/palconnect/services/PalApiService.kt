package com.example.palconnect.services

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.basicAuth
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod

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

