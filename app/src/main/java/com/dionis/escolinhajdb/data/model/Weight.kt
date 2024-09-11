package com.dionis.escolinhajdb.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Weight(

    val weight: Float = 0f,
    val date: Date?,

) : Parcelable
