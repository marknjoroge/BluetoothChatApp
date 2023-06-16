package com.example.bluetoothchatapp

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bluetoothchatapp.contollers.Routes
import com.example.bluetoothchatapp.ui.theme.BluetoothChatAppTheme
import com.example.bluetoothchatapp.views.ChatPage
import com.example.bluetoothchatapp.views.MainPage
import java.lang.reflect.Method

const val REQUEST_ENABLE_BLUETOOTH = 1
const val REQUEST_DISCOVERABLE = 2
const val DISCOVERABLE_DURATION = 300

const val ABC_TAG = "ABC"

val theDevices = HashMap<String, String>()

class MainActivity : ComponentActivity() {

    private var bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    var device: BluetoothDevice? = null

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BluetoothChatAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavigationAppHost(navController = navController)
                }
            }
        }

        requestPermissions()

        Global.myMacAddress = bluetoothAdapter.address

        Log.i(ABC_TAG, Global.myMacAddress)

        val pairedDevices: Set<BluetoothDevice> = bluetoothAdapter.bondedDevices
        pairedDevices.forEach { device ->
            theDevices[device.name] = device.address
        }

        // TODO: Use this to check if we can get something from it
        Log.i("ABC", "Devices: " + bluetoothAdapter.bondedDevices)

        Log.i("ABC", theDevices.toString())

        val connectedDevice: BluetoothDevice? = getCurrentConnectedBluetoothDevice(pairedDevices)

        if (connectedDevice != null) {
            Log.i(ABC_TAG, "Connected to ${connectedDevice.name}")

            Global.connectedDevice = mapOf(connectedDevice.name to connectedDevice.address)
            Global.connectedBluetoothDevice = connectedDevice

            val deviceName = if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            } else {

            }
            connectedDevice.name
            val deviceAddress = connectedDevice.address
            Log.i(ABC_TAG, "Connected Device Name: $deviceName, Address: $deviceAddress")
        } else {
            Log.i(ABC_TAG, "Connected Device Name: none")
        }
    }

    fun checkPermissions(): Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)
            != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN)
            != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
            != PackageManager.PERMISSION_GRANTED
        ) return false
        return true
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestPermissions() {
        if (!checkPermissions()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.BLUETOOTH_CONNECT
                ),
                REQUEST_ENABLE_BLUETOOTH
            )
        } else {
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {
                // Bluetooth permissions granted, proceed with Bluetooth operations
                // ...
            } else {
                // Bluetooth permissions denied
                // Handle the denied permission case
                // ...
            }
        }
    }

    fun getCurrentConnectedBluetoothDevice(devices: Set<BluetoothDevice>): BluetoothDevice? {
        devices.forEach { device ->
            if (isConnected(device)) return device
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
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationAppHost(navController: NavHostController) {
    val ctx = LocalContext.current

    NavHost(navController = navController, startDestination = "main") {
        composable(Routes.MainPage.route) { MainPage(navController) }
        composable(Routes.ChatPage.route) { navBackStackEntry ->
            val macAddress = navBackStackEntry.arguments?.getString("macAddress")
            if (macAddress == null) {
                Toast.makeText(ctx, "Address not provided", Toast.LENGTH_SHORT).show()
            } else {
                ChatPage(navController = navController, macAddress = macAddress)
            }
        }
    }
}
