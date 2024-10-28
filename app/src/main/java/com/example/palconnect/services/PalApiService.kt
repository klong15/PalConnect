package com.example.palconnect.services

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.basicAuth
import io.ktor.client.request.headers
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json

class PalApiService {

    private var _client = HttpClient(CIO)
    private var _ip: String = ""
    private var _password: String = ""
    val ignoreKeysJson = Json { ignoreUnknownKeys }

    suspend fun getServerInfo(
        error: suspend (HttpResponse) -> Unit = {},
        exception: suspend (Exception) -> Unit = {},
        success: suspend (HttpResponse) -> Unit = {},
    ): HttpResponse? {
        return makeGetRequest(
            endpoint= "/v1/api/info",
            error = error,
            exception = exception,
            success = success
        )
    }

    suspend fun getPlayers(
        error: suspend (HttpResponse) -> Unit = {},
        exception: suspend (Exception) -> Unit = {},
        success: suspend (HttpResponse) -> Unit = {},
    ): HttpResponse? {
        return makeGetRequest(
            endpoint= "/v1/api/players",
            error = error,
            exception = exception,
            success = success
        )
    }

    suspend fun getServerMetrics(
        error: suspend (HttpResponse) -> Unit = {},
        exception: suspend (Exception) -> Unit = {},
        success: suspend (HttpResponse) -> Unit = {},
    ): HttpResponse? {
        return makeGetRequest(
            endpoint= "/v1/api/metrics",
            error = error,
            exception = exception,
            success = success
        )
    }

    suspend fun announceMessage(
        message: String,
        error: suspend (HttpResponse) -> Unit = {},
        exception: suspend (Exception) -> Unit = {},
        success: suspend (HttpResponse) -> Unit = {},
    ): HttpResponse? {
        val jsonMessage = "{\n  \"message\": \"$message\"\n}"
        return makePostRequest("/v1/api/announce", jsonMessage)
    }

    suspend fun saveWorld(
        error: suspend (HttpResponse) -> Unit = {},
        exception: suspend (Exception) -> Unit = {},
        success: suspend (HttpResponse) -> Unit = {},
    ): HttpResponse? {
        return makePostRequest("/v1/api/save", "")
    }

    private suspend fun makeGetRequest(
        endpoint: String,
        error: suspend (HttpResponse) -> Unit = {},
        exception: suspend (Exception) -> Unit = { e -> },
        success: suspend (HttpResponse) -> Unit = {}
    ): HttpResponse? {

        var status = -1
        var response: HttpResponse? = null

        try {
            response = _client.request("http://${_ip}${endpoint}") {
                method = HttpMethod.Get
                headers {
                    basicAuth("admin", _password)
                }
            }

            status = if (response.status == HttpStatusCode.OK) 0
            else 1

        } catch (e: Exception) {
            exception(e)
        }

        //Execute callbacks outside of try block so exceptions caused here aren't caught and potentially ignored
        if(status == 0) success(response!!)
        if(status == 1) error(response!!)

        return response
    }

    private suspend fun makePostRequest(
        endpoint: String,
        body: String,
        error: suspend (HttpResponse) -> Unit = {},
        exception: suspend (Exception) -> Unit = { e -> },
        success: suspend (HttpResponse) -> Unit = {}
    ): HttpResponse? {

        var status = -1
        var response: HttpResponse? = null

        try {
            response = _client.request("http://${_ip}${endpoint}") {
                method = HttpMethod.Post
                headers {
                    basicAuth("admin", _password)
                }
                setBody(body)
            }

            status = if (response.status == HttpStatusCode.OK) 0
            else 1

        } catch (e: Exception) {
            exception(e)
        }

        //Execute callbacks outside of try block so exceptions caused here aren't caught and potentially ignored
        if(status == 0) success(response!!)
        if(status == 1) error(response!!)

        return response
    }

    fun setServerInfo(ip: String, password: String) {
        _ip = ip
        _password = password
    }
}

