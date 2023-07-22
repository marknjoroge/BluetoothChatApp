package com.example.bluetoothchatapp.services

import android.util.Log
import com.example.bluetoothchatapp.ABC_TAG
import com.example.bluetoothchatapp.utils.Global
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.security.MessageDigest

class FirebaseService(
    private var from: String,
    private var to: String
) {
    private val separator = "˥"
    private val interpunct = "·"

    private val textsCollection = "texts"

    private val db = Firebase.firestore

    private var myDocId = ""

    init {
        myDocId = createDocId()

        addDummyData()
    }

    fun sendText(
        text: String
    ) {
        getTexts()
        val theTexts: String = Global.globalMessages + "$separator$from $interpunct $text"

        db.collection(textsCollection).document(myDocId).update("messages", theTexts)
        getTexts()
    }

    private fun createDocId(): String {
        val p1 = from.replace(":", "")
        val p2 = to.replace(":", "")

        val sum = p1 + p2
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val hashBytes = messageDigest.digest(sum.toByteArray())

        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    private fun addDummyData() {
        val messagePost = hashMapOf(
            "messages" to ""
        )

        val documentRef = db.collection(textsCollection).document(myDocId)

        documentRef.set(messagePost)
            .addOnSuccessListener {
                Log.d(ABC_TAG, "DocumentSnapshot added with ID: $myDocId")
            }
            .addOnFailureListener {
                Log.w(ABC_TAG, "Error adding document")
            }
    }

    fun getTexts() {
        db.collection(textsCollection)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if (document.id == myDocId) {
                        Global.globalMessages = document.data["messages"] as String
                    }
                }
            }
    }
}