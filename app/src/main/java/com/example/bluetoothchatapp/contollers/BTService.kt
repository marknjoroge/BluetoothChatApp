package com.example.bluetoothchatapp.contollers

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.bluetoothchatapp.ABC_TAG
import com.example.bluetoothchatapp.Global
import com.example.bluetoothchatapp.REQUEST_ENABLE_BLUETOOTH
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

private const val TAG = "MY_APP_DEBUG_TAG"

// Defines several constants used when transmitting messages between the
// service and the UI.
const val MESSAGE_READ: Int = 0
const val MESSAGE_WRITE: Int = 1
const val MESSAGE_TOAST: Int = 2
// ... (Add other message types here as needed.)

@RequiresApi(Build.VERSION_CODES.S)
class BTService (private val handler: Handler, private val context: Context) {

    private var socket: BluetoothSocket
    private var connectedThread: ConnectedThread

    fun getCurrentConnectedBluetoothDevice(devices: List<BluetoothDevice>): BluetoothDevice? {
        for(device in devices) {
            if(isConnected(device)) return device
        }
        return null
    }

    private fun isConnected(device: BluetoothDevice): Boolean {
        return try {
            val m: Method = device.javaClass.getMethod("isConnected")
            m.invoke(device) as Boolean
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun writeToThread(message: String) {
        connectedThread.write(message.toByteArray())
    }

    fun createBluetoothSocket(connectedDevice: BluetoothDevice): BluetoothSocket? {
        try {
            val createRfcommSocket = connectedDevice.javaClass.getMethod(
                "createRfcommSocket", Int::class.javaPrimitiveType
            )
            val socket = createRfcommSocket.invoke(connectedDevice, 1) as BluetoothSocket
            if(!socket.isConnected) Log.i(ABC_TAG, "BluetoothSocket: ${socket.toString()}")
            return socket
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
        return null
    }

    fun getMacAddress() {

    }

    init {
        socket = Global.connectedBluetoothDevice?.let { createBluetoothSocket(it) }!!
//        socket = connectSocket(socket = socket)
        connectedThread = ConnectedThread(mmSocket = socket)

        connectedThread.start()
    }

    inner class ConnectedThread(private var mmSocket: BluetoothSocket) : Thread() {

        private var mmInStream: InputStream = mmSocket.inputStream
        private var mmOutStream: OutputStream = mmSocket.outputStream
        private var mmBuffer: ByteArray = ByteArray(1024) // mmBuffer store for the stream

        @RequiresApi(Build.VERSION_CODES.S)
        override fun run() {

//            requestPermissions()

            if (ActivityCompat.checkSelfPermission(
                    context,
//                    Manifest.permission.BLUETOOTH_CONNECT
//                ) != PackageManager.PERMISSION_GRANTED
//                || ActivityCompat.checkSelfPermission(
//                    context,
//                    Manifest.permission.BLUETOOTH_ADMIN
//                ) != PackageManager.PERMISSION_GRANTED
//                || ActivityCompat.checkSelfPermission(
//                    context,
                    Manifest.permission.BLUETOOTH
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.i(ABC_TAG, "No permission. Cannot connect socket")
                requestPermissions()
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            } else {
                Log.i(ABC_TAG, "permission. Can connect")
            }
            mmSocket.connect()
            if(mmSocket.isConnected) Log.i(ABC_TAG, "socket connected")
            else Log.i(ABC_TAG, "socket not connected")

            Log.i(ABC_TAG, "Socket name: ${mmSocket.remoteDevice}")
            var numBytes: Int

            while (true) {
                numBytes = try {
                    mmInStream.read(mmBuffer)
                } catch (e: IOException) {
                    Log.d(TAG, "Input stream was disconnected", e)
                    break
                }

                val readMsg = handler.obtainMessage(
                    MESSAGE_READ, numBytes, -1,
                    mmBuffer
                )
                readMsg.sendToTarget()
            }
        }

        // Call this from the main activity to send data to the remote device.
        @RequiresApi(Build.VERSION_CODES.S)
        fun write(bytes: ByteArray) {
            try {
                Log.i(ABC_TAG, "Socket == $mmSocket")
                if(mmOutStream != null) Log.i(ABC_TAG, "mmoutstream null")
                else mmOutStream.write(bytes)
            } catch (e: IOException) {
                Log.e(TAG, "Error occurred when sending data", e)

                // Send a failure message back to the activity.
                val writeErrorMsg = handler.obtainMessage(MESSAGE_TOAST)
                val bundle = Bundle().apply {
                    putString("toast", "Couldn't send data to the other device")
                }
                writeErrorMsg.data = bundle
                handler.sendMessage(writeErrorMsg)
                return
            }

            // Share the sent message with the UI activity.
            val writtenMsg = handler.obtainMessage(
                MESSAGE_WRITE, -1, -1, mmBuffer
            )
            writtenMsg.sendToTarget()
        }

        // Call this method from the main activity to shut down the connection.
        fun cancel() {
            try {
                mmSocket.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the connect socket", e)
            }
        }

    }

//    @RequiresApi(Build.VERSION_CODES.S)
//    fun connectSocket(socket: BluetoothSocket): BluetoothSocket {
//        if (ActivityCompat.checkSelfPermission(
//                context,
//                Manifest.permission.BLUETOOTH_CONNECT
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            Log.i(ABC_TAG, "No permission. Can not connect socket")
//            requestPermissions()
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//
//        }
//        socket.connect()
//        if(socket.isConnected) Log.i(ABC_TAG, "socket connected")
//        else Log.i(ABC_TAG, "socket not connected")
//
//        return socket
//    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestPermissions() {

        val permissions = arrayOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH
        )

        val requestCode = 1
        Log.i(ABC_TAG, "Requesting permissions")
        ActivityCompat.requestPermissions(context as Activity, permissions, requestCode)
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(ABC_TAG, "Permissions granted")
                } else {
                    Log.i(ABC_TAG, "Permissions denied")
                }
            }
        }
    }
}