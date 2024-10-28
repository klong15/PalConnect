package com.example.palconnect.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import com.example.palconnect.NavigationManager
import com.example.palconnect.PalConnectApp
import com.example.palconnect.Route
import com.example.palconnect.models.ServerInfoModel
import com.example.palconnect.models.ServerMetricsModel
import com.example.palconnect.services.PalApiService
import io.ktor.client.call.body
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import androidx.lifecycle.viewmodel.viewModelFactory
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.isActive

private const val METRICS_UPDATE_CADENCE: Long = 5000L

data class OverviewUiState(
    var infoModel: ServerInfoModel = ServerInfoModel(),
    var metricsModel: ServerMetricsModel = ServerMetricsModel(),
    var errorMessage: String = "",
    var awaitingAnnounceResponse: Boolean = false,
    var saveWorldButtonEnable: Boolean = true,
//    private val _saveWorldButtonFlow: MutableSharedFlow<Boolean> = MutableSharedFlow<Boolean>(),
//    val saveWorldButtonFlow: SharedFlow<Boolean> = _saveWorldButtonFlow
)

class OverviewViewModel(
    private val palApiService: PalApiService,
    private val navigationManager: NavigationManager
): ViewModel() {

    companion object {

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                OverviewViewModel(
                    PalConnectApp.palModule.palApiService,
                    PalConnectApp.palModule.palNavigationManager
                )
            }
        }
    }

    var myvalue = 0

    init {
        myvalue = 1
    }

    private var updateJob: Job? = null

    private val _uiState = MutableStateFlow(OverviewUiState())
    val uiState: StateFlow<OverviewUiState> = _uiState.asStateFlow()

    private val _announcementResponseMessage = MutableSharedFlow<String>()
    val announcementResponseMessage: SharedFlow<String> = _announcementResponseMessage

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
            updateJob = viewModelScope.launch {
                while (isActive) {
                    println("Fetching Metrics!")
                    palApiService.getServerMetrics() { response ->
                        _uiState.update { currentState ->
                            currentState.copy(
                                metricsModel = Json.decodeFromString(response.body<String>())
                            )
                        }
                    }
                    delay(METRICS_UPDATE_CADENCE)
                }
            }
        }
    }

    fun onStop(route: Route?) {
        updateJob?.cancel()
    }

    fun makeAnnouncementClicked(message: String) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy( awaitingAnnounceResponse = true)
            }
            var responseMessage = ""
            val response = palApiService.announceMessage(
                message= message,
                error = { response ->
                    responseMessage = "Announcement failed to send."
                },
                exception = { e ->
                    responseMessage = "Announcement failed to send."
                }
            ) {
                responseMessage = "Announcement sent successfully."
            }

            _uiState.update { currentState ->
                currentState.copy(
                    awaitingAnnounceResponse = false
                )
            }
            _announcementResponseMessage.emit(responseMessage)
        }
    }

    fun saveWorldClicked(context: Context) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    saveWorldButtonEnable = false
                )
            }
            var toastMessage = "ERROR: World save was NOT initiated."
            val response = palApiService.saveWorld() {
                toastMessage = "World save has been initiated."
            }

            Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()

            _uiState.update { currentState ->
                currentState.copy(
                    saveWorldButtonEnable = true
                )
            }
        }
    }

    fun playersClicked() {
        navigationManager.navigateTo(Route.Players)
    }
}