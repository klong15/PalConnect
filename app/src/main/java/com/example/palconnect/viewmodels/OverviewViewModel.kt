package com.example.palconnect.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.palconnect.NavigationManager
import com.example.palconnect.Route
import com.example.palconnect.models.ServerInfoModel
import com.example.palconnect.services.PalApiService
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

private const val PLAYER_UPDATE_CADENCE: Long = 5000L

data class OverviewUiState(
    var infoModel: ServerInfoModel = ServerInfoModel(),
    var errorMessage: String = ""
)

class OverviewViewModel(
    private val palApiService: PalApiService,
    private val navigationManager: NavigationManager
): ViewModel() {


    private val _uiState = MutableStateFlow(OverviewUiState())
    val uiState: StateFlow<OverviewUiState> = _uiState.asStateFlow()

    var updatePlayers = true;

    fun onStart(route: Route?) {
        println("onStart")

        if(route == Route.Overview) {
            // Fetch initial data

            if(_uiState.value.infoModel.servername.isEmpty()) {
                println("Fetching Server Info!")
                viewModelScope.launch {
                    palApiService.getServerInfo() { response ->
                        _uiState.update { currentState ->
                            currentState.copy(
                                infoModel = Json.decodeFromString(response.body<String>())
                            )
                        }
                    }
                }
            }
        }

        if(route == Route.Overview || route == Route.Players) {
            updatePlayers = true
            viewModelScope.launch {
                while (updatePlayers) {
                    println("Fetching Player Info!")
                    delay(PLAYER_UPDATE_CADENCE)
                }
            }
        }
    }

    fun onStop(route: Route?) {
        println("onStop")
        updatePlayers = false
    }

    private suspend fun updateServerInfo() {
        try {
            val response = palApiService.getServerInfo()

            if (response.status == HttpStatusCode.OK) {
                _uiState.update { currentState ->
                    currentState.copy(
                        infoModel = Json.decodeFromString(response.body<String>())
                    )
                }
            } else {
                _uiState.update { currentState ->
                    currentState.copy(
                        errorMessage = "Unauthorized!"
                    )
                }
            }
        } catch (e: Exception) {
            _uiState.update { currentState ->
                currentState.copy(
                    errorMessage = "Exception Occurred!"
                )
            }
        }
    }
}