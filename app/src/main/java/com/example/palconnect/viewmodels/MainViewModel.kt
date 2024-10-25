package com.example.palconnect.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.palconnect.services.PalApiService
import com.example.palconnect.services.PalRetroService
import kotlinx.coroutines.launch

class MainViewModel(
    private val palApiService: PalApiService
): ViewModel() {

    private var _ip by mutableStateOf("192.168.0.201")
    val ip: String get() = _ip

    private var _password by mutableStateOf("doob")
    val password: String get() = _password

    private var _canSubmit by mutableStateOf(_ip.isNotEmpty() && _password.isNotEmpty())
    val canSubmit: Boolean get() = _canSubmit

    private var _result by mutableStateOf("Results shown here")
    val result: String get() = _result

    private var _isLoadingConfig by mutableStateOf(false)
    val isLoadingConfig: Boolean get() = _isLoadingConfig

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

        getServerInfo()
    }

    fun getServerInfo() {
        viewModelScope.launch {
            try {
                _isLoadingConfig = true
                palApiService.buildRetroService(_ip)
                val result = palApiService.getPhotos()
                _result = result
            } catch (e: Exception){
                println(e)
            }
            _isLoadingConfig = false
        }
    }
}