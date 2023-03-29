package com.dionis.escolinhajdb.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Coach(

    var id: String = "",
    val name: String = "",
    val photo: String = "",
    val function: String = "",
    val subFunction: String = "",
    val birth: String = "",
    val genre: String = "",
    val contact: String = "",
    val memberSince: String = "",

    val email: String = "",

    ) : Parcelable
