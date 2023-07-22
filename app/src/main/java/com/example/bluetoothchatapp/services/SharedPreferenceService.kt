package com.example.bluetoothchatapp.services
import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceService(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)

    fun hasConnected(): Boolean {
        return sharedPreferences.contains("hadConnected")
    }

    fun setHasConnected() {
        sharedPreferences.edit().putBoolean("hadConnected", true).apply()
    }
}
