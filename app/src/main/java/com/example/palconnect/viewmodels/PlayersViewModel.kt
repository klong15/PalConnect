package com.example.palconnect.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.palconnect.NavigationManager
import com.example.palconnect.PalConnectApp
import com.example.palconnect.models.PlayersModel
import com.example.palconnect.services.PalApiService
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

data class PlayersUiState(
    var isLoadingData: Boolean = false,
    var errorMessage: String = "",
    var playersModel: PlayersModel = PlayersModel()
)

class PlayersViewModel(
    private val palApiService: PalApiService,
    private val navigationManager: NavigationManager
): ViewModel() {

    companion object {

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayersViewModel(
                    PalConnectApp.palModule.palApiService,
                    PalConnectApp.palModule.palNavigationManager
                )
            }
        }
    }

    private val _uiState = MutableStateFlow(PlayersUiState())
    val uiState: StateFlow<PlayersUiState> = _uiState.asStateFlow()

    fun onStart() {
        if(_uiState.value.playersModel.players.isEmpty()) {
            refreshPlayers()
        }
    }

    private fun refreshPlayers() {
        viewModelScope.launch {

            _uiState.update { currentState ->
                currentState.copy(
                    isLoadingData = true,
                    errorMessage = ""
                )
            }

            var players: PlayersModel? = null
            val response = palApiService.getPlayers() { response ->
                var realPlayers: PlayersModel = Json.decodeFromString(response.body<String>())
                players = PlayersModel(
                    players = realPlayers.players + PlayersModel.createDummyData(50)
                )
            }

            var errorMessage = ""
            if(response == null || response.status != HttpStatusCode.OK) {
                errorMessage = "Error: Couldn't load players"
            }

            _uiState.update { currentState ->
                currentState.copy(
                    isLoadingData = false,
                    errorMessage = errorMessage,
                    playersModel = players ?: PlayersModel()
                )
            }
        }
    }
}