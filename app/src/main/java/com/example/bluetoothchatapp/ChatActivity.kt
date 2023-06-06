package com.example.bluetoothchatapp

import BTService
import MESSAGE_READ
import MESSAGE_TOAST
import MESSAGE_WRITE
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.os.Build
import android.os.Handler
import androidx.annotation.RequiresApi
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ChatActivity(theirMacAddress: String, context: Context) {

    private var localContext: Context? = null
    private var macAddress: String = ""
    var messages = HashMap<String, String>()
    var bluetoothAdapter: BluetoothAdapter? = null

    private var btService: BTService? = null

    val handler = Handler(Handler.Callback { msg ->
        // Handle messages received from the service
        when (msg.what) {
            MESSAGE_READ -> {
                // Process the received data
                val data = msg.obj as ByteArray
                // Update UI with received data
            }
            MESSAGE_WRITE -> {
                // Handle write operation completed
            }
            MESSAGE_TOAST -> {
                // Handle toast message from service
                val toastMessage = msg.data.getString("toast")
                // Display toast message
            }
        }
        true
    })

    init {
        btService = BTService(handler)
        localContext = context
        macAddress = theirMacAddress
        btService = BTService(handler)

        btService
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTime(): String? {
        val currentTime = LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        return currentTime.format(formatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendText(text: String) {
        addText(text)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addText(text: String) {
        messages[getTime().toString()] = "$macAddress · $text"
    }

//    fun sendDataOverBluetooth(deviceAddress: String, data: String) {
//        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
//        val device: BluetoothDevice? = bluetoothAdapter.getRemoteDevice(deviceAddress)
//
//        // UUID for the Serial Port Profile (SPP)
//        val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
//
//        val socket: Unit = if (localContext?.let {
//                ActivityCompat.checkSelfPermission(
//                    it,
//                    Manifest.permission.BLUETOOTH_CONNECT
//                )
//            } != PackageManager.PERMISSION_GRANTED
//        ) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return
//        } else {
//
//        }
//        device?.createRfcommSocketToServiceRecord(uuid)
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
}