package com.example.bluetoothchatapp

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.bluetoothchatapp.ui.theme.myBubbleColor
import com.example.bluetoothchatapp.ui.theme.myTextColor
import com.example.bluetoothchatapp.ui.theme.theirBubbleColor
import com.example.bluetoothchatapp.ui.theme.theirTextColor

var myAddress = "20:A4:F6:EA"

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatPage(navController: NavHostController, macAddress: String) {

    val context = LocalContext.current
    val chatActivity = ChatActivity(macAddress, context = context)
    var myText by remember { mutableStateOf("Yoo whatsup") }

    var messages  = chatActivity.messages

    chatActivity.populateMessages()

    val listState = rememberLazyListState()
    val keyAtIndex = Global.connectedDevice.keys.elementAt(0)
    val connectedMac = Global.connectedDevice[keyAtIndex]

    if (macAddress != connectedMac) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "This device is not connected.",
                modifier = Modifier.padding(16.dp)
            )
        }
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "Chat with $macAddress")
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Filled.ArrowBack, "backIcon", tint = myBubbleColor)
                        }
                    },
                    modifier = Modifier
                        .background(Color.White)
                )
            },
            content = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(it)
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .padding(bottom = 80.dp)
                            .padding(horizontal = 20.dp)
                            .fillMaxWidth()
                    ) {
                        items(messages) { message ->
                            val parts = message.split(" · ")

                            if (parts.size == 2) {
                                val address = parts[0].trim()
                                val text = parts[1].trim()
                                ChatBubble(address = address, message = text)
                            } else {
                                println("Invalid sentence format")
                            }
                        }
                    }
                    LaunchedEffect(messages.size) {
                        listState.animateScrollToItem(messages.size)
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 3.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = myText,
                                modifier = Modifier.height(60.dp),
                                placeholder = { Text("New message", color = Color.DarkGray) },
                                onValueChange = { newText: String -> myText = newText },
                                textStyle = TextStyle(color = Color.DarkGray)
                            )
                            IconButton(
                                onClick = {
                                    chatActivity.sendText(myText)
                                    messages = chatActivity.messages
                                    Log.i("ABC", messages.size.toString())
                                    myText = ""
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Send",
                                    modifier = Modifier
                                        .size(70.dp),
                                    tint = myBubbleColor
                                )
                            }
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun ChatBubble(address: String, message: String) {
    var bubbleColor = myBubbleColor
    var textColor = myTextColor

    if (address != myAddress) {
        bubbleColor = theirBubbleColor
        textColor = theirTextColor
    }

    Column(
        modifier = Modifier
            .padding(vertical = 7.dp)
            .defaultMinSize(minHeight = 60.dp, minWidth = 100.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bubbleColor)
            .padding(13.dp)
    ) {
        Text(
            text = message,
            color = textColor,
            fontSize = 17.sp
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun Preview() {
    var ctx: Context = LocalContext.current
    var navController: NavHostController = NavHostController(ctx)
    ChatPage(navController = navController, macAddress = "")
}
