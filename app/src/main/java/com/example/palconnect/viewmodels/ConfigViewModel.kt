package com.example.palconnect.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.palconnect.NavigationManager
import com.example.palconnect.Route
import com.example.palconnect.models.ServerInfoModel
import com.example.palconnect.services.PalApiService
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

data class ConfigUiState(
    var ipField: String = "192.168.0.201:8212",
    var passwordField: String = "doob",
    var canSubmit: Boolean = ipField.isNotEmpty() && passwordField.isNotEmpty(),
    var isLoading: Boolean = false,
    var infoModel: ServerInfoModel = ServerInfoModel(),
    var message: String = ""
)

class ConfigViewModel(
    private val palApiService: PalApiService,
    private val navigationManager: NavigationManager
): ViewModel() {

//    var model: MutableLiveData<ServerInfoModel> = MutableLiveData<ServerInfoModel>(ServerInfoModel())

    private val _configUiState = MutableStateFlow(ConfigUiState())
    val configUiState: StateFlow<ConfigUiState> = _configUiState.asStateFlow()

    fun ipTextChanged(newIp: String) {
        _configUiState.update { currentState ->
            currentState.copy(
                ipField = newIp,
                canSubmit = newIp.isNotEmpty() && currentState.passwordField.isNotEmpty()
            )
        }
    }

    fun passwordTextChanged(newPassword: String) {
        _configUiState.update { currentState ->
            currentState.copy(
                passwordField = newPassword,
                canSubmit = currentState.ipField.isNotEmpty() && newPassword.isNotEmpty()
            )
        }
    }

    fun submitted() {
        if(!_configUiState.value.canSubmit) return
//
        palApiService.setServerInfo(_configUiState.value.ipField, _configUiState.value.passwordField)
        getServerInfo()
    }

    fun getServerInfo() {
        viewModelScope.launch {
            var hasError: Boolean = false;
            try {
                setIsLoading(true)
                val result = palApiService.getServerInfo()
                if(result.status == HttpStatusCode.OK) {

                    _configUiState.update { currentState ->
                        currentState.copy(
                            infoModel = Json.decodeFromString(result.body<String>())
                        )
                    }
                } else {
                    hasError = true;
                }
            } catch (e: Exception){
                hasError = true
            }

            if(hasError) {
                _configUiState.update { currentState ->
                    currentState.copy(
                        message = if (hasError) "ERROR" else _configUiState.value.infoModel.description
                    )
                }
            } else {
                //Navigate to next page
                navigationManager.navigateToAsync(Route.Overview)
                delay(1000)
                setIsLoading(false)

            }

            setIsLoading(false)
        }
    }

    private fun setIsLoading(loading: Boolean) {
        _configUiState.update { currentState ->
            currentState.copy(
                isLoading = loading,
            )
        }
    }
}