package com.example.faketinder

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

data class User(
    var id: String="",
    var name: String = "",
    var age: Int = 0,
    val gender: String = "",
    var description: String = "",
    var interestedIn: String = ""
)

