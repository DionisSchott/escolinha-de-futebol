package com.dionis.escolinhajdb.data.model

import android.os.Parcelable
import android.provider.ContactsContract
import com.google.firebase.firestore.ServerTimestamp
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.parcelize.Parcelize
import org.w3c.dom.Text
import java.util.*

@Parcelize
data class Player(
    var id: String = "",
    val playerName: String = "",
    val position: String = "",
    val responsibleName: String = "",
    val responsibleType: String = "",
    val playersBirth: String = "",
    val genre: String = "",
    val images: List<String> = arrayListOf(),
    val healthNotes: String = "",
    val skillsNotes: String = "",
    val weight: Float = 0f,
    val height: Float = 0f,
    val contacts: String = "",
    val category: String = "",
    @ServerTimestamp
    val insertionDate: Date = Date(),
) : Parcelable