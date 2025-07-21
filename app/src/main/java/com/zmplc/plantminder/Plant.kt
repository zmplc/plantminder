package com.zmplc.plantminder

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Plant(
    val id: String = "",
    val nome: String = "",
    val nomeScientifico: String = "",
    val descrizione: String = "",
    val tipo: String = "",
    val temperatura: String = "",
    val luce: String = "",
    val acqua: String = "",
    val intervalloAnnaffiatura: Int = 0
) : Parcelable
