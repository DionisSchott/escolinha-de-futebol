package com.dionis.escolinhajdb.data.model

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Events(
    var id: String = "",
    val title: String = "",
    val albumLink: String = "",
    val description: String = "",
    val eventType: String = "",
 //   val listOfNeeds: List<String> = arrayListOf(),
    val addedBy: String = "",

    @ServerTimestamp
    val date: Date? = null,

) : Parcelable
