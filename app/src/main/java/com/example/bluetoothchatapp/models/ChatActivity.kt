package com.example.bluetoothchatapp.models

import com.example.bluetoothchatapp.contollers.BTService
import com.example.bluetoothchatapp.contollers.MESSAGE_READ
import com.example.bluetoothchatapp.contollers.MESSAGE_TOAST
import com.example.bluetoothchatapp.contollers.MESSAGE_WRITE
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.os.Build
import android.os.Handler
import androidx.annotation.RequiresApi
import com.example.bluetoothchatapp.Global
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class ChatActivity(theirMacAddress: String, context: Context) {

    private var localContext: Context? = null

//    private var myMacAddress: String = Global.myMacAddress
    private var myMacAddress: String = "20:A4:F6:EA"
    private var theirMacAddress: String = ""

    var messages = ArrayList<String>()
    var bluetoothAdapter: BluetoothAdapter? = null

    private var btService: BTService? = null


    fun populateMessages() {
        messages.add("20:A4:F6:EA · Knock Knock")
        messages.add("34:34:5D:A3 · Who's there?")
        messages.add("20:A4:F6:EA · Amos")
        messages.add("34:34:5D:A3 · Amos who?")
        messages.add("20:A4:F6:EA · A mosquito bit me!")
        messages.add("20:A4:F6:EA · Knock Knock")
        messages.add("34:34:5D:A3 · Who's there?")
        messages.add("20:A4:F6:EA · Olive")
        messages.add("34:34:5D:A3 · Olive who?")
        messages.add("20:A4:F6:EA · Olive you and I miss you!")
        messages.add("20:A4:F6:EA · Knock Knock")
        messages.add("34:34:5D:A3 · Who's there?")
        messages.add("20:A4:F6:EA · Harry")
        messages.add("34:34:5D:A3 · Harry who?")
        messages.add("20:A4:F6:EA · Harry up and answer the door!")
        messages.add("20:A4:F6:EA · Knock Knock")
        messages.add("34:34:5D:A3 · Who's there?")
        messages.add("20:A4:F6:EA · Boo")
        messages.add("34:34:5D:A3 · Boo who?")
        messages.add("20:A4:F6:EA · Don't cry, it's just a joke!")
        messages.add("20:A4:F6:EA · Knock Knock")
        messages.add("34:34:5D:A3 · Who's there?")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    val handler = Handler(Handler.Callback { msg ->
        // Handle messages received from the service
        when (msg.what) {
            MESSAGE_READ -> {
                // Process the received data
                val data = msg.obj as ByteArray

                addText(data.toString())
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
        this.theirMacAddress = theirMacAddress
        btService = BTService(handler, context)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTime(): String? {
        val currentTime = LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        return currentTime.format(formatter)
    }


    @RequiresApi(Build.VERSION_CODES.S)
    fun sendText(text: String) {
        addText(text)
        btService?.writeToThread(text)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addText(text: String, macAddress: String = myMacAddress) {
        messages.add("$macAddress · $text")
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