package com.example.bluetoothchatapp.utils

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.bluetoothchatapp.ABC_TAG
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

const val MESSAGE_STATE_CHANGED = 0
const val MESSAGE_READ = 1
const val MESSAGE_WRITE = 2
const val MESSAGE_DEVICE_NAME = 3
const val MESSAGE_TOAST = 4

const val TOAST = "toast"
const val DEVICE_NAME = "deviceName"

const val STATE_NONE = 0
const val STATE_LISTEN = 1
const val STATE_CONNECTING = 2
const val STATE_CONNECTED = 3


class ChatUtils(private val macToConnect: String, private val context: Context, private val handler: Handler) {
    private val bluetoothAdapter: BluetoothAdapter
    private var connectThread: ConnectThread? = null
    private var acceptThread: AcceptThread? = null
    private var connectedThread: ConnectedThread? = null
    private val APP_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66")
    private val APP_NAME = "BluetoothChatApp"
    private var theState: Int

    init {
        theState = STATE_NONE
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        Log.i(ABC_TAG, "Initiated chat utils")
        connect(bluetoothAdapter.getRemoteDevice(macToConnect))
    }

    fun getState(): Int {
        return theState
    }

    @Synchronized
    fun setState(theState: Int) {
        this.theState = theState
        handler.obtainMessage(MESSAGE_STATE_CHANGED, theState, -1).sendToTarget()
    }

    @Synchronized
    private fun start() {
        if (connectThread != null) {
            connectThread!!.cancel()
            connectThread = null
        }
        if (acceptThread == null) {
            acceptThread = AcceptThread()
            acceptThread!!.start()
        }
        if (connectedThread != null) {
            connectedThread!!.cancel()
            connectedThread = null
        }
        setState(STATE_LISTEN)
    }

    @Synchronized
    fun stop() {
        if (connectThread != null) {
            connectThread!!.cancel()
            connectThread = null
        }
        if (acceptThread != null) {
            acceptThread!!.cancel()
            acceptThread = null
        }
        if (connectedThread != null) {
            connectedThread!!.cancel()
            connectedThread = null
        }
        setState(STATE_NONE)
    }

    fun connect(device: BluetoothDevice) {
        if (theState == STATE_CONNECTING) {
            connectThread!!.cancel()
            connectThread = null
        }
        connectThread = ConnectThread(device)
        connectThread!!.start()
        if (connectedThread != null) {
            connectedThread!!.cancel()
            connectedThread = null
        }
        setState(STATE_CONNECTING)
    }

    fun write(buffer: ByteArray?) {
        var connThread: ConnectedThread?
        synchronized(this) {
            if (theState != STATE_CONNECTED) {
                Log.i(ABC_TAG, "Not connected")
                return
            }
            Log.i(ABC_TAG, "Connected")
            connThread = connectedThread
        }
        connThread!!.write(buffer)
    }

    private inner class AcceptThread : Thread() {
        private val serverSocket: BluetoothServerSocket?

        init {
            var tmp: BluetoothServerSocket? = null
            try {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.

                }
                Log.d(ABC_TAG, "Accept->Constructor")
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME, APP_UUID)
            } catch (e: IOException) {
                Log.e(ABC_TAG, "Accept->Constructor $e")
            }
            serverSocket = tmp
        }

        override fun run() {
            var socket: BluetoothSocket? = null
            try {
                socket = serverSocket!!.accept()
                Log.d(ABC_TAG, "Accept serversocket")
            } catch (e: IOException) {
                Log.e("Accept->Run", e.toString())
                try {
                    serverSocket!!.close()
                } catch (e1: IOException) {
                    Log.e("Accept->Close", e.toString())
                }
            }
            if (socket != null) {
                when (theState) {
                    STATE_LISTEN, STATE_CONNECTING -> connected(socket, socket.remoteDevice)
                    STATE_NONE, STATE_CONNECTED -> try {
                        socket.close()
                    } catch (e: IOException) {
                        Log.e(ABC_TAG, "Accept->CloseSocket ${e.toString()}")
                    }

                    else -> {}
                }
            }
        }

        fun cancel() {
            try {
                serverSocket!!.close()
            } catch (e: IOException) {
                Log.e(ABC_TAG, "Accept->CloseServer ${e.toString()}")
            }
        }
    }

    private inner class ConnectThread(private val device: BluetoothDevice) : Thread() {
        private val socket: BluetoothSocket?

        init {
            var tmp: BluetoothSocket? = null

            Log.d(ABC_TAG, "starting conn thread")
            try {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.

                }
                tmp = device.createRfcommSocketToServiceRecord(APP_UUID)
                Log.d(ABC_TAG, "starting conn thread 2")
            } catch (e: IOException) {
                Log.e(ABC_TAG, "Connect->Constructor ${e.toString()}")
            }
            socket = tmp

            start()
        }

        override fun run() {
            try {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                socket!!.connect()
                Log.d(ABC_TAG, "connect thread running")
                Log.i(ABC_TAG, socket.isConnected.toString())
            } catch (e: IOException) {
                Log.d(ABC_TAG, "connect thread running error")
                Log.e(ABC_TAG, e.toString())
                try {
                    socket!!.close()
                } catch (e1: IOException) {
                    Log.e(ABC_TAG, e.toString())
                }
                connectionFailed()
                return
            }
            synchronized(context) { connectThread = null }
            connected(socket, device)
        }

        fun cancel() {
            try {
                socket!!.close()
            } catch (e: IOException) {
                Log.e(ABC_TAG, e.toString())
            }
        }
    }

    private inner class ConnectedThread(private val socket: BluetoothSocket) : Thread() {
        private val inputStream: InputStream?
        private val outputStream: OutputStream?

        init {
            Log.d(ABC_TAG, "connected thread")
            var tmpIn: InputStream? = null
            var tmpOut: OutputStream? = null
            try {
                tmpIn = socket.inputStream
                tmpOut = socket.outputStream
            } catch (e: IOException) {
            }
            inputStream = tmpIn
            outputStream = tmpOut
        }

        override fun run() {
            Log.d(ABC_TAG, "connected thread running")
            val buffer = ByteArray(1024)
            val bytes: Int
            try {
                bytes = inputStream!!.read(buffer)
                handler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget()
            } catch (e: IOException) {
                connectionLost()
            }
        }

        fun write(buffer: ByteArray?) {
            Log.d(ABC_TAG, "connected thread running")
            try {
                outputStream!!.write(buffer)
                handler.obtainMessage(MESSAGE_WRITE, -1, -1, buffer).sendToTarget()
            } catch (e: IOException) {
            }
        }

        fun cancel() {
            try {
                socket.close()
            } catch (e: IOException) {
            }
        }
    }

    private fun connectionLost() {
        val message = handler.obtainMessage(MESSAGE_TOAST)
        val bundle = Bundle()
        bundle.putString(TOAST, "Connection Lost")
        message.data = bundle
        handler.sendMessage(message)
        start()
    }

    @Synchronized
    private fun connectionFailed() {
        val message = handler.obtainMessage(MESSAGE_TOAST)
        val bundle = Bundle()
        bundle.putString(TOAST, "Cant connect to the device")
        message.data = bundle
        handler.sendMessage(message)
        start()
    }

    @Synchronized
    private fun connected(socket: BluetoothSocket?, device: BluetoothDevice) {
        if (connectThread != null) {
            connectThread!!.cancel()
            connectThread = null
        }
        if (connectedThread != null) {
            connectedThread!!.cancel()
            connectedThread = null
        }
        connectedThread = socket?.let { ConnectedThread(it) }
        connectedThread!!.start()
        val message = handler.obtainMessage(MESSAGE_DEVICE_NAME)
        val bundle = Bundle()
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        bundle.putString(DEVICE_NAME, device.name)
        message.data = bundle
        handler.sendMessage(message)
        setState(STATE_CONNECTED)
    }
}