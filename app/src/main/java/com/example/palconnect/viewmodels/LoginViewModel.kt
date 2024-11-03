package com.example.palconnect.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.palconnect.NavigationManager
import com.example.palconnect.PalConnectApp
import com.example.palconnect.services.PalApiService
import com.example.palconnect.services.PalDataStore
import com.example.palconnect.services.PalUtilityService

class LoginViewModel(
    private val palApiService: PalApiService,
    private val navigationManager: NavigationManager,
    private val dataStore: PalDataStore,
    private val utilityService: PalUtilityService
): ViewModel() {

    companion object {

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                LoginViewModel(
                    PalConnectApp.palModule.palApiService,
                    PalConnectApp.palModule.palNavigationManager,
                    PalConnectApp.palModule.palDataStore,
                    PalConnectApp.palModule.palUtilityService
                )
            }
        }
    }


}