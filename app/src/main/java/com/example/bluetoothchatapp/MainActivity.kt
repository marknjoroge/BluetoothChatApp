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
import com.example.bluetoothchatapp.ui.theme.BluetoothChatAppTheme

const val REQUEST_ENABLE_BLUETOOTH = 1
const val REQUEST_DISCOVERABLE = 2
const val DISCOVERABLE_DURATION = 300

const val ABC_TAG = "ABC"

val theDevices = HashMap<String, String>()

class MainActivity : ComponentActivity() {

    var bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

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
        if (bluetoothAdapter.isEnabled) {
            Log.i("ABC", "Bluetooth enabled")
        } else {
            Log.i("ABC", "Turn on bluetooth to use app")
        }

        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        pairedDevices?.forEach { device ->
            theDevices[device.name] = device.address
        }



        // TODO: Use this to check if we can get something from it
        Log.i("ABC", "Devices: " + bluetoothAdapter.bondedDevices)

        Log.i("ABC", theDevices.toString())
        Toast.makeText(this, bluetoothAdapter.address, Toast.LENGTH_LONG).show()

        val connectedDevice: BluetoothDevice? = getCurrentConnectedBluetoothDevice(this)

        if (connectedDevice != null) {
            Log.i(ABC_TAG, "Connected ${connectedDevice.toString()}")
            val deviceName = if (ActivityCompat.checkSelfPermission(
                    this,
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
        ) return true
        return false
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)
            != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN)
            != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
            != PackageManager.PERMISSION_GRANTED
        ) {

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
//            Toast.makeText(this, "Already Granted", Toast.LENGTH_LONG).show()
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

    fun getCurrentConnectedBluetoothDevice(context: Context): BluetoothDevice? {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val bluetoothManager = bluetoothAdapter?.getProfileProxy(this, object : BluetoothProfile.ServiceListener {
            override fun onServiceDisconnected(profile: Int) {}

            override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
                if (profile == BluetoothProfile.A2DP) {
                    val connectedDevices: List<BluetoothDevice>? = proxy.connectedDevices
                    if (!connectedDevices.isNullOrEmpty()) {
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
                        Log.d(ABC_TAG, "Connected: ${connectedDevices.size}")
                        Global.connectedDevice = mapOf(connectedDevices[0].name to connectedDevices[0].address)
                        // Perform any necessary actions with the connected device
                    }
                }
                bluetoothAdapter.closeProfileProxy(profile, proxy)
            }
        }, BluetoothProfile.A2DP)

        return null
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
