package com.example.bluetoothchatapp.views

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.bluetoothchatapp.ABC_TAG
import com.example.bluetoothchatapp.utils.Global
import com.example.bluetoothchatapp.contollers.Routes
import com.example.bluetoothchatapp.theDevices
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import java.util.Timer
import java.util.TimerTask

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun MainPage(navController: NavHostController) {
    val context = LocalContext.current

    var connectedName by remember { mutableStateOf( "") }
    var connectedMac by remember { mutableStateOf( "") }

    val interval: Long = 3 * 1000
    Log.e("ABC","Hello World")

    Timer().scheduleAtFixedRate(object : TimerTask() {
        override fun run() {
            if(Global.connectedDevice.keys.elementAt(0) != "") {
                connectedName = Global.connectedDevice.keys.elementAt(0)
                connectedMac = Global.connectedDevice[connectedName].toString()
            } else {
                Log.i(ABC_TAG, "Global.connectedDevice.size.toString()")
            }
        }
    },1000, interval)

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
                "Select Device",
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp),
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray
            )
            Text(
                text = "Connected Device",
                Modifier.padding(top = 30.dp, bottom = 5.dp),
                color = Color.Gray,
                fontSize = 12.sp
            )
            if (connectedName == "") {
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
                    DeviceCapsule(navController, name = connectedName, macAddress = connectedMac)
                }
            }
            Text(
                text = "Other Devices",
                Modifier.padding(vertical = 5.dp),
                color = Color.Gray,
                fontSize = 12.sp
            )
            for ((k, v) in theDevices) {
                if (k == "") continue
                DeviceCapsule(navController, name = k, macAddress = v)
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun DeviceCapsule(navController: NavHostController, name: String = "", macAddress: String = "") {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    var doNotShowRationale by rememberSaveable {
        mutableStateOf(false)
    }

    val btConnectPermissionsState =
        rememberPermissionState(permission = android.Manifest.permission.BLUETOOTH_CONNECT)

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission Accepted: Do something
            btConnectPermissionsState.launchPermissionRequest()
            Log.d(ABC_TAG,"PERMISSION GRANTED")
        } else {
            // Permission Denied: Do something
            btConnectPermissionsState.launchPermissionRequest()
            Log.d(ABC_TAG,"PERMISSION DENIED")
        }
    }

    Column(
        Modifier
            .padding(vertical = 2.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                // Check permission
                Log.i(ABC_TAG, "${PackageManager.PERMISSION_GRANTED} Checking permissions")
                when (PackageManager.PERMISSION_GRANTED) {
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) -> {
                        navController.navigate(Routes.ChatPage.createRoute(macAddress))
                        // Some works that require permission
//                        Log.d(ABC_TAG,"Code requires permission")
//                        launcher.launch(Manifest.permission.BLUETOOTH_CONNECT)
                    }

                    else -> {
                        // Asking for permission
                        Log.d(ABC_TAG, "Asking for permission")
                        launcher.launch(Manifest.permission.BLUETOOTH_CONNECT)
                        navController.navigate(Routes.ChatPage.createRoute(macAddress))
                    }
                }
            }
            .background(color = Color(0, 0, 0, 30))
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Text(
            text = name,
            color = Color.DarkGray
        )
        Text(
            text = macAddress,
            color = Color.Gray,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Preview
@Composable
fun Previewer() {
    val ctx = LocalContext.current
    val navController = NavHostController(ctx)
    MainPage(navController = navController)
}
