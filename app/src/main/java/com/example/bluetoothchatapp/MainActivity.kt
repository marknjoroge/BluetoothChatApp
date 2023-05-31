package com.example.bluetoothchatapp

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bluetoothchatapp.ui.theme.BluetoothChatAppTheme

const val REQUEST_ENABLE_BLUETOOTH = 1
const val REQUEST_DISCOVERABLE = 2
const val DISCOVERABLE_DURATION = 300

val theDevices = HashMap<String, String>()

class MainActivity : ComponentActivity() {

    var bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

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

        Log.i("ABC", "Yo")
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
            println("Connected Device Name: $deviceName, Address: $deviceAddress")
        } else {
            println("No device currently connected.")
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

@Composable
fun MainPage(navController: NavHostController) {
    val context = LocalContext.current



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(enabled = true, state = ScrollState(0))
                .padding(horizontal = 20.dp, vertical = 60.dp)
        ) {
            Text(
                text = "Connect to a Device",
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp),
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
            Text(
                text = "Connected Devices",
                Modifier.padding(top = 30.dp, bottom = 5.dp),
                color = Color.Gray,
                fontSize = 12.sp
            )
            if (Global.connectedDevice.isEmpty()) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(30.dp)

                ) {
                    IconButton(
                        onClick = {
                            val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
                            context.startActivity(intent)
                        },
                        Modifier.align(Alignment.Center)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "",
                            tint = Color.Gray,
                            modifier = Modifier
                                .scale(4f)
                                .blur(1.dp)
                        )
                    }
                }
            } else {
                for ((k, v) in Global.connectedDevice) {
                    DeviceCapsule(navController, name = k, macAddress = v)
                }
            }
            Text(
                text = "Paired Devices",
                Modifier.padding(vertical = 5.dp),
                color = Color.Gray,
                fontSize = 12.sp
            )
            for ((k, v) in theDevices) {
                DeviceCapsule(navController, name = k, macAddress = v)
            }
        }
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .background(Color.White)
//                .padding(horizontal = 20.dp)
//                .align(Alignment.BottomCenter)
//        ) {
//            Button(
//                onClick = {
//                    val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
//                    context.startActivity(intent)
//                },
//                modifier = Modifier
//                    .fillMaxWidth()
//            ) {
//                Text(text = "Search for open connections")
//            }
//        }
    }
}

@Composable
fun DeviceCapsule(navController: NavHostController, name: String = "", macAddress: String = "") {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    Column(
        Modifier
            .clickable {
                navController.navigate(Routes.ChatPage.createRoute(macAddress))
            }
            .padding(vertical = 2.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(color = Color(0, 0, 0, 30))
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = name,
                color = Color.DarkGray
            )
            IconButton(
                onClick = { expanded = !expanded },
                modifier = Modifier.padding(0.dp),
            ) {
                Icon(
                    modifier = Modifier.padding(0.dp),
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More",
                    tint = Color.Gray,
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                DropdownMenuItem(
                    text = { Text("Load") },
                    onClick = { Toast.makeText(context, "Load", Toast.LENGTH_SHORT).show() }
                )
                DropdownMenuItem(
                    text = { Text("Save") },
                    onClick = { Toast.makeText(context, "Save", Toast.LENGTH_SHORT).show() }
                )
            }
        }
        Text(
            text = macAddress,
            color = Color.Gray,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview
@Composable
fun Previewer() {
    var ctx = LocalContext.current
    var navController: NavHostController = NavHostController(ctx)
    MainPage(navController = navController)
}
