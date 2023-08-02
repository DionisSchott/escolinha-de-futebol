package com.dionis.escolinhajdb.data.model

import android.os.Parcelable
import android.provider.ContactsContract
import com.google.firebase.firestore.ServerTimestamp
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.parcelize.Parcelize
import org.w3c.dom.Text
import java.time.LocalDate
import java.util.*

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
    val height: Float = 0f,
    val measurementDate: Date? = null,
    val bloodType: String = "",
    val healthNotes: String = "",
    @ServerTimestamp
    val startDate: Date? = null,
    val isActive: Boolean = true,
    val departureDate: Date? = null,
) : Parcelable