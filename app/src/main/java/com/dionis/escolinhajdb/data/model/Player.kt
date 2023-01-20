package com.dionis.escolinhajdb.data.model

import android.os.Parcelable
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Player(
    var id: String = "",
    val playerName: String = "",
    val responsibleName: String = "",
    val playersBirth: String = "",
    val images: List<String> = arrayListOf(),
) : Parcelable