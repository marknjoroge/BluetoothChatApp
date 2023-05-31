//package com.example.bluetoothchatapp
//
//import android.os.Build
//import androidx.annotation.RequiresApi
//import java.time.LocalTime
//import java.time.format.DateTimeFormatter
//
//import android.bluetooth.BluetoothAdapter
//import android.bluetooth.BluetoothDevice
//import android.bluetooth.BluetoothServerSocket
//import android.bluetooth.BluetoothSocket
//import java.io.BufferedReader
//import java.io.IOException
//import java.io.InputStreamReader
//import java.io.OutputStream
//import java.util.*
//
//
//class others {
//    @RequiresApi(Build.VERSION_CODES.O)
//    fun getTime(): String? {
//        val currentTime = LocalTime.now()
//        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
//        return currentTime.format(formatter)
//    }
//
//    // Function to send data over Bluetooth
//    fun sendDataOverBluetooth(deviceAddress: String, data: String) {
//        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
//        val device: BluetoothDevice? = bluetoothAdapter.getRemoteDevice(deviceAddress)
//
//        // UUID for the Serial Port Profile (SPP)
//        val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
//
//        val socket: BluetoothSocket? = device?.createRfcommSocketToServiceRecord(uuid)
//
//        socket?.let {
//            try {
//                it.connect()
//                val outputStream: OutputStream = it.outputStream
//                outputStream.write(data.toByteArray())
//
//                // Perform any necessary cleanup after sending the data
//
//            } catch (e: Exception) {
//                e.printStackTrace()
//            } finally {
//                it.close()
//            }
//        }
//    }
//
//    // Usage example
//    fun main() {
//        val deviceAddress = "12:34:56:78:90:AB" // Replace with the Bluetooth device address
//        val dataToSend = "Hello, Bluetooth!" // Replace with the data you want to send
//
//        sendDataOverBluetooth(deviceAddress, dataToSend)
//    }
//
//
//
//
//    fun receiveDataOverBluetooth() {
//        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
//        val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
//        val serverSocket: BluetoothServerSocket? = bluetoothAdapter.listenUsingRfcommWithServiceRecord("BluetoothServer", uuid)
//
//        serverSocket?.let {
//            try {
//                val socket: BluetoothSocket = it.accept()
//                val inputStream = socket.inputStream
//                val bufferedReader = BufferedReader(InputStreamReader(inputStream))
//                var message: String?
//
//                while (true) {
//                    message = bufferedReader.readLine()
//                    if (message != null) {
//                        // Process the received message
//                        println("Received message: $message")
//                    } else {
//                        break
//                    }
//                }
//
//                // Perform any necessary cleanup after receiving messages
//
//            } catch (e: IOException) {
//                e.printStackTrace()
//            } finally {
//                it.close()
//            }
//        }
//    }
//
//    // Usage example
//    fun main() {
//        receiveDataOverBluetooth()
//    }
//
//}