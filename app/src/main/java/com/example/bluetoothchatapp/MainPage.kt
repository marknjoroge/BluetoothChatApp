package com.example.bluetoothchatapp

import android.content.Intent
import android.provider.Settings
import android.widget.Toast
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
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.navigation.NavHostController


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
                "Connect to a Device",
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
            if (Global.connectedDevice.size <= 1) {
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
//                        onClick = {
//                            navController.navigate(Routes.ChatPage.createRoute("macAddress"))
//                        },
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
                if (k == "") continue
                DeviceCapsule(navController, name = k, macAddress = v)
            }
        }
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

@Preview
@Composable
fun Previewer() {
    var ctx = LocalContext.current
    var navController: NavHostController = NavHostController(ctx)
    MainPage(navController = navController)
}
