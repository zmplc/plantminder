package com.zmplc.plantminder

import java.util.Date
import java.util.Calendar
import java.util.concurrent.TimeUnit

// Classe che uso per gestire e mostrare nel GardenPlantDetailFragment il pallino dello stato e
// ultima innaffiatura + prossima innaffiatura

data class InfoInnaffiatura(
    val ultimaInnaffiatura: Date?,
    val intervalloAnnaffiatura: Int
) {

    // Se la pianta deve essere annaffiata oggi o è in ritardo -> true
    fun daAnnaffiare(): Boolean {
        ultimaInnaffiatura ?: return true // se ultimaInnaffiatura è null restituisco true

        val oggi = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val ultima = Calendar.getInstance().apply {
            time = ultimaInnaffiatura
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        ultima.add(Calendar.DAY_OF_YEAR, intervalloAnnaffiatura)
        return !ultima.after(oggi) // restituisco true se la prossima data è oggi o prima e la pianta è quindi da annaffiare
    }

    // Calcolo il numero di giorni dall'ultima innaffiatura
    fun giorniDallUltimaAnnaffiatura(): Long {
        ultimaInnaffiatura ?: return -1

        val calOggi = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val calUltima = Calendar.getInstance().apply {
            time = ultimaInnaffiatura
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val diffMillis = calOggi.timeInMillis - calUltima.timeInMillis
        return TimeUnit.MILLISECONDS.toDays(diffMillis)
    }

    // Calcolo prossima data innaffiatura
    fun prossimaAnnaffiatura(): Date? {
        return ultimaInnaffiatura?.let {
            val calendar = Calendar.getInstance().apply {
                time = it
                add(Calendar.DAY_OF_YEAR, intervalloAnnaffiatura)

                // Imposta ora, minuti, secondi e millisecondi a zero
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            calendar.time
        }
    }
}
