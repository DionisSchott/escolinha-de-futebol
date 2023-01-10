package com.dionis.escolinhajdb.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Player(

    var id: String = "",
    val playerName: String = "",
    val responsibleName: String = "",
    val playersBirth: String = "",

    ) : Parcelable