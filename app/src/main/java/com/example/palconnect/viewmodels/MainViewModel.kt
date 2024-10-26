package com.example.palconnect.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.palconnect.models.ServerInfoModel
import com.example.palconnect.services.PalApiService
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class MainViewModel(
    private val palApiService: PalApiService
): ViewModel() {

    private var _ip by mutableStateOf("192.168.0.201:8212")
    val ip: String get() = _ip

    private var _password by mutableStateOf("doob")
    val password: String get() = _password

    private var _canSubmit by mutableStateOf(_ip.isNotEmpty() && _password.isNotEmpty())
    val canSubmit: Boolean get() = _canSubmit

    private var _result by mutableStateOf("Results shown here")
    val result: String get() = _result

    private var _isLoadingConfig by mutableStateOf(false)
    val isLoadingConfig: Boolean get() = _isLoadingConfig

    var model: MutableLiveData<ServerInfoModel> = MutableLiveData<ServerInfoModel>(ServerInfoModel())

    fun ipTextChanged(newIp: String) {
        _ip = newIp

        _canSubmit = _ip.isNotEmpty() && _password.isNotEmpty()
    }

    fun passwordTextChanged(newPassword: String) {
        _password = newPassword

        _canSubmit = _ip.isNotEmpty() && _password.isNotEmpty()
    }

    fun submitted() {
        if(!_canSubmit) return

        palApiService.setServerInfo(_ip, _password)
        getServerInfo()
    }

    fun getServerInfo() {
        viewModelScope.launch {
            var hasError: Boolean = false;
            try {
                _isLoadingConfig = true
                val result = palApiService.getServerInfo()
                if(result.status == HttpStatusCode.OK) {
                    model.postValue(Json.decodeFromString(result.body<String>()))
                } else {
                    hasError = true;
                }
            } catch (e: Exception){
                hasError = true
            }
            _isLoadingConfig = false

            if(hasError) _result = "ERROR"
        }
    }
}