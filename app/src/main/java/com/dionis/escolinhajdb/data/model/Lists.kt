package com.dionis.escolinhajdb.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Lists(

    val position: List<String> = arrayListOf(),
    val blood: List<String> = arrayListOf(),
    val function: List<String> = arrayListOf(),
    val category: List<String> = arrayListOf(),

    ) : Parcelable
