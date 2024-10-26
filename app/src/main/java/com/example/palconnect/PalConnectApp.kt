package com.example.palconnect

import android.app.Application
import com.example.palconnect.services.PalApiModule

class PalConnectApp: Application() {

    companion object {
        lateinit var palApiModule: PalApiModule
    }

    override fun onCreate() {
        super.onCreate()
        palApiModule = PalApiModule(this)
    }
}