package com.example.palconnect.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.palconnect.NavigationManager
import com.example.palconnect.PalConnectApp
import com.example.palconnect.Route
import com.example.palconnect.models.ServerInfoModel
import com.example.palconnect.services.PalApiService
import com.example.palconnect.services.PalDataStore
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

data class ConfigUiState(
    var pageTitle: String = "Players",
    var ipField: String = "192.168.0.201:8212",
    var passwordField: String = "doob",
    var canSubmit: Boolean = ipField.isNotEmpty() && passwordField.isNotEmpty(),
    var isLoading: Boolean = false,
    var infoModel: ServerInfoModel = ServerInfoModel(),
    var message: String = "",
)

class ConfigViewModel(
    private val palApiService: PalApiService,
    private val navigationManager: NavigationManager,
    private val dataStore: PalDataStore
): ViewModel() {

    companion object {

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ConfigViewModel(
                    PalConnectApp.palModule.palApiService,
                    PalConnectApp.palModule.palNavigationManager,
                    PalConnectApp.palModule.palDataStore
                )
            }
        }
    }

    private val _uiState = MutableStateFlow(ConfigUiState())
    val uiState: StateFlow<ConfigUiState> = _uiState.asStateFlow()

    fun ipTextChanged(newIp: String) {
        _uiState.update { currentState ->
            currentState.copy(
                ipField = newIp,
                canSubmit = newIp.isNotEmpty() && currentState.passwordField.isNotEmpty()
            )
        }
    }

    fun passwordTextChanged(newPassword: String) {
        _uiState.update { currentState ->
            currentState.copy(
                passwordField = newPassword,
                canSubmit = currentState.ipField.isNotEmpty() && newPassword.isNotEmpty()
            )
        }
    }

    fun submitted() {
        if(!_uiState.value.canSubmit) return

        val ip = _uiState.value.ipField
        val password = _uiState.value.passwordField
        palApiService.setServerInfo(ip, password)

        viewModelScope.launch {
            getServerInfo {
                dataStore.saveLoginConfig(ip, password)
            }
        }
    }

    suspend fun getServerInfo(showError: Boolean = true, onSuccess: suspend () -> Unit = {}) {

        var hasError: Boolean = true;
        setIsLoading(true)
        val response = palApiService.getServerInfo() { result ->
            hasError = false
            _uiState.update { currentState ->
                currentState.copy(
                    infoModel = Json.decodeFromString(result.body<String>())
                )
            }
            onSuccess()
        }

        if(hasError) {
            if(showError) {
                _uiState.update { currentState ->
                    currentState.copy(
                        message = "Error validating server"
                    )
                }
            }
        } else {
            //Navigate to next page
            navigationManager.navigateToAsync(Route.PopBackStack)
        }

        setIsLoading(false)
    }

    private fun setIsLoading(loading: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                isLoading = loading,
                message = if (loading) "" else currentState.message,
            )
        }
    }
}