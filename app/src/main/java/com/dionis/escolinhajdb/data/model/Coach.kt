package com.dionis.escolinhajdb.data.model

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Coach(

    var id: String = "",
    val name: String = "",
    val email: String = "",
    val photo: String = "",
    val function: String = "",
    val category: String = "",
    val birth: String = "",
    val genre: String = "",
    val contact: String = "",
    val memberSince: String = ""

) : Parcelable
