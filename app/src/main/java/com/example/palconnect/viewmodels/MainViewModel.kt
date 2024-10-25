package com.example.palconnect.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {

    private var _ip by mutableStateOf("")
    val ip: String
        get() = _ip
    private var _password by mutableStateOf("")
    val password: String
        get() = _password

    private var _canSubmit by mutableStateOf(false)
    val canSubmit: Boolean
        get() = _canSubmit

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

        println("IP Address: ${_ip}\nPassword: ${_password}")
    }
}