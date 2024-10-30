package com.example.palconnect.services

import android.content.Context
import android.widget.Toast

class PalUtilityService(val context: Context) {

    fun showToastText(text: String, duration: Int) {
        Toast.makeText(context, text, duration).show()
    }

    fun showToastText(resId: Int, duration: Int) {
        Toast.makeText(context, resId, duration).show()
    }

    fun getString(resId: Int): String {
        return context.getString(resId)
    }
}