package com.dionis.escolinhajdb.data.model

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Player(
    var id: String = "",
    val images: String = "",
    val playerName: String = "",
    val preferredName: String = "",
    val playersBirth: String = "",
    val genre: String = "",
    val responsibleName: String = "",
    val responsibleType: String = "",
    val contacts: String = "",
    val addedBy: String = "",
    //  val endereço: String = "",

    val position: String = "",
    val preferredFoot: String = "",
    val skillsNotes: String = "",
    val category: String = "",

    val weight: Float = 0f,
    val weighingDate: Date? = null,
    val height: Float = 0f,
    val measurementDate: Date? = null,
    val bloodType: String = "",
    val healthNotes: String = "",
    @ServerTimestamp
    val startDate: Date? = null,
    val isActive: Boolean = true,
    val departureDate: Date? = null,
) : Parcelable