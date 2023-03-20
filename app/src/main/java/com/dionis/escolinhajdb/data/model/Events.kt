package com.dionis.escolinhajdb.data.model

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Events(
    var id: String = "",
    val title: String = "",
    val image: String = "",
    val albumLink: String = "",
    val description: String = "",
    val listOfNeeds: List<String> = arrayListOf(),


    @ServerTimestamp
    val date: Date,

) : Parcelable
