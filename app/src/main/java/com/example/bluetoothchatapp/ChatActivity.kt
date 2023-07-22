package com.example.bluetoothchatapp

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.os.Build
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.bluetoothchatapp.contollers.MESSAGE_READ
import com.example.bluetoothchatapp.contollers.MESSAGE_TOAST
import com.example.bluetoothchatapp.contollers.MESSAGE_WRITE
import com.example.bluetoothchatapp.services.FirebaseService
import com.example.bluetoothchatapp.services.SharedPreferenceService
import com.example.bluetoothchatapp.utils.ChatUtils
import com.example.bluetoothchatapp.utils.Global
import com.example.bluetoothchatapp.utils.TOAST
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.S)
class ChatActivity(theirMacAddress: String, private var context: Context) {

    //    private var myMacAddress: String = Global.myMacAddress
    private var myMacAddress: String = "20:A4:F6:EA"
    private var theirMacAddress: String = ""
    private val separator = "˥"
    private val sharedPreferenceService = SharedPreferenceService(context)

    var messages = ArrayList<String>()
    var messagesString = ""
    lateinit var bluetoothAdapter: BluetoothAdapter

    private lateinit var chatUtils: ChatUtils
    private lateinit var firebaseService: FirebaseService

    fun populateMessages(): ArrayList<String> {
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

        messagesString = ""

        getMessages()

        var words = messagesString.split(separator)
        for (word in words) {
            messages.add(word)
        }

        return messages
    }

    fun separateAndStore(input: String): ArrayList<String> {
        val resultList = ArrayList<String>()
        val splitStrings = input.split("˥")

        for (str in splitStrings) {
            val trimmedStr = str.trim()
            if (trimmedStr.isNotEmpty()) {
                resultList.add(trimmedStr)
            }
        }

        messages = resultList

        return resultList
    }

    fun getMessages() {
        firebaseService.getTexts()
    }

    private val handler = Handler { message ->
        when (message.what) {
            MESSAGE_WRITE -> {
                Log.d(ABC_TAG, "handler writing")
                val buffer1 = message.obj as ByteArray
                val outputBuffer = String(buffer1)
                Log.i(ABC_TAG, outputBuffer)
                addText(text = outputBuffer, macAddress = myMacAddress)
            }

            MESSAGE_READ -> {
                Log.d(ABC_TAG, "handler reading")
                val buffer = message.obj as ByteArray
                val inputBuffer = String(buffer, 0, message.arg1)
                Log.i(ABC_TAG, inputBuffer)
                addText(text = inputBuffer, macAddress = theirMacAddress)
            }

            MESSAGE_TOAST -> {

                Log.d(ABC_TAG, "handler toasting")
                Toast.makeText(
                    context,
                    message.data.getString(TOAST),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        false
    }

    init {
        this.theirMacAddress = theirMacAddress
        firebaseService = FirebaseService(myMacAddress, theirMacAddress)
        firebaseService.sendText(text = "Knock Knock")
        if(!sharedPreferenceService.hasConnected()) {
            sharedPreferenceService.setHasConnected()
        }
//        chatUtils = ChatUtils(theirMacAddress, context, handler)
//        btService = BTService(handler, context)
    }

    private fun initBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Toast.makeText(context, "No Bluetooth Found", Toast.LENGTH_SHORT).show()
        }
    }

    //    @RequiresApi(Build.VERSION_CODES.O)
    fun getTime(): String? {
        val currentTime = LocalTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        return currentTime.format(formatter)
    }


    @RequiresApi(Build.VERSION_CODES.S)
    fun sendText(text: String) {
        addText(text)
//        chatUtils.write(text.toByteArray())
        firebaseService.sendText(text)
    }

    fun addText(text: String, macAddress: String = myMacAddress) {
        messages.add("$macAddress · $text")
    }
}
