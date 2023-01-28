package com.dionis.escolinhajdb.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Coach(

    var id: String = "",
    val name: String = "",
    val category: String = "",
    val email: String = "",

) : Parcelable
