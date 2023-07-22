package com.example.bluetoothchatapp.utils

import android.app.Application
import android.bluetooth.BluetoothDevice

class Global : Application() {
    companion object {
        @JvmField
        var connectedDevice: Map<String, String> = mapOf("" to "")
        var connectedBluetoothDevice: BluetoothDevice? = null
        var myMacAddress: String = ""

        var globalMessages = ""
    }
}