package com.zmplc.plantminder

import android.os.Parcelable
import java.util.Date
import kotlinx.parcelize.Parcelize

@Parcelize
data class GardenPlant(
    val id: String = "",
    val nomePersonalizzato: String = "",
    val nome: String = "",
    val posizione: String = "",
    val intervalloAnnaffiatura: Int = 0,
    val ultimaInnaffiatura: Date? = null
) : Parcelable
