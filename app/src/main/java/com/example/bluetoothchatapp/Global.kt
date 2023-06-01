package com.example.bluetoothchatapp

import android.app.Application
import androidx.lifecycle.MutableLiveData

class Global : Application() {
    companion object {
        @JvmField
        var connectedDevice: Map<String, String> = mapOf("" to "")
    }
}