package com.example.palconnect

import android.app.Application
import android.content.Context
import com.example.palconnect.services.PalApiService
import com.example.palconnect.services.PalDataStore

class PalConnectApp: Application() {

    companion object {
        lateinit var palModule: PalModule
    }

    override fun onCreate() {
        super.onCreate()
        palModule = PalModule(this)
    }
}

class PalModule(
    private val appContext: Context
) {

    val palApiService: PalApiService by lazy {
        PalApiService()
    }

    val palNavigationManager: NavigationManager by lazy {
        NavigationManager()
    }

    val palDataStore: PalDataStore by lazy {
        PalDataStore(appContext)
    }
}