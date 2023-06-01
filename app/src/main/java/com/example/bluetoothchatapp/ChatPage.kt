package com.example.bluetoothchatapp

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ScrollState
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
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.bluetoothchatapp.ui.theme.myBubbleColor
import com.example.bluetoothchatapp.ui.theme.myTextColor
import com.example.bluetoothchatapp.ui.theme.theirBubbleColor
import com.example.bluetoothchatapp.ui.theme.theirTextColor

var myAddress = "foo"
var messages: ArrayList<String> = ArrayList()

fun populateMessages() {
    messages.add("Knock knock")
    messages.add("Who's there?")
    messages.add("Boo")
    messages.add("Boo who?")
    messages.add("Don't cry, it's just a joke!")
    messages.add("Haha, got me there!")
    messages.add("Glad you liked it!")
    messages.add("Do you have another one?")
    messages.add("Sure! Knock knock")
    messages.add("Who's there?")
    messages.add("Lettuce")
    messages.add("Lettuce who?")
    messages.add("Lettuce in, it's freezing out here!")
    messages.add("Haha, good one!")
    messages.add("I'm glad you liked it. Jokes always lighten the mood!")
    messages.add("They sure do! Got any more?")
    messages.add("Absolutely! Knock knock")
    messages.add("Who's there?")
    messages.add("Cash")
    messages.add("Cash who?")
    messages.add("No thanks, I prefer peanuts!")
    messages.add("Haha, that's a good one too!")
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatPage(navController: NavHostController, macAddress: String) {

    populateMessages()
    val context = LocalContext.current
    val chatActivity = ChatActivity(macAddress, context = context)
    var myText by remember { mutableStateOf("Yoo whatsup") }

//    var messages by remember { mutableStateOf(chatActivity.messages) }

    val listState = rememberLazyListState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Chat with $macAddress")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, "backIcon")
                    }
                },
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
                ) {
//                    for ((k, v) in messages) {
//                        val parts = v.split(" · ")
//
//                        if (parts.size == 3) {
//                            val address = parts[0].trim()
//                            val text = parts[1].trim()
//                            ChatBubble(address = address, message = text)
//                        } else {
//                            println("Invalid sentence format")
//                        }
//                    }
                    items(messages) {message ->
//                        for (message in messages) {
                            ChatBubble(address = "121212121212", message = message)
//                        }
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
                        OutlinedTextField (
                            value = myText,
                            modifier = Modifier.height(60.dp),
                            placeholder = {Text("New message")},
                            onValueChange = {newText: String -> myText = newText }
                        )
                        IconButton(
                            onClick = {
                                chatActivity.sendText(myText)
//                                messages = chatActivity.messages
                                myText = ""
                            },
//                            modifier = Modifier.height(80.dp),
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
//        Text(
//            text = address,
//            color = textColor,
//            fontWeight = FontWeight.Bold
//        )
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
    var ctx = LocalContext.current
    var navController: NavHostController = NavHostController(ctx)
    ChatPage(navController = navController, macAddress = "")
}
