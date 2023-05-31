package com.example.bluetoothchatapp

sealed class Routes(val route: String) {
    object MainPage: Routes("main")
    object DummyPage: Routes("dummy")
    object ChatPage: Routes("chat/{macAddress}") {
        fun createRoute(macAddress: String) = "chat/$macAddress"
    }
}