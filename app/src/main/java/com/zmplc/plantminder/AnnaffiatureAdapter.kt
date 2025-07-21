package com.zmplc.plantminder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*

class AnnaffiatureAdapter(
    private val gardenPlants: List<GardenPlant>,
    private val onItemClick: (GardenPlant) -> Unit
) : RecyclerView.Adapter<AnnaffiatureAdapter.AnnaffiatureViewHolder>() {

    class AnnaffiatureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomePianta: TextView = itemView.findViewById(R.id.nomePiantaTextView)
        val intervalloAnnaffiatura: TextView = itemView.findViewById(R.id.intervalloAnnaffiaturaTextView)
        val statoAnnaffiatura: View = itemView.findViewById(R.id.statoAnnaffiatura)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnnaffiatureViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_plant_annaffiatura, parent, false)
        return AnnaffiatureViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnnaffiatureViewHolder, position: Int) {
        val gardenPlant = gardenPlants[position]
        holder.nomePianta.text = gardenPlant.nomePersonalizzato
        holder.intervalloAnnaffiatura.text = "Annaffiare ogni ${gardenPlant.intervalloAnnaffiatura} giorni"

        // Data ultima annaffiatura
        val formatter = SimpleDateFormat("d MMMM yyyy", Locale.ITALIAN)
        val ultimaInnaffiatura = gardenPlant.ultimaInnaffiatura
        holder.itemView.findViewById<TextView>(R.id.ultimaAnnaffiaturaTextView).text =
            if (ultimaInnaffiatura != null) {
                "Ultima annaffiatura: ${formatter.format(ultimaInnaffiatura)}"
            } else {
                "Mai annaffiata"
            }

        // Gestione colore del pallino in base allo stato annaffiatura
        val intervallo = gardenPlant.intervalloAnnaffiatura
        val ultima = gardenPlant.ultimaInnaffiatura
        val oggi = Calendar.getInstance()

        val drawableRes = when {
            ultima == null -> R.drawable.status_circle // grigio: mai annaffiata
            else -> {
                val dataUltima = Calendar.getInstance().apply { time = ultima }
                dataUltima.add(Calendar.DAY_OF_YEAR, intervallo)

                when {
                    isSameDay(dataUltima, oggi) -> R.drawable.status_circle_blue   // da annaffiare oggi
                    dataUltima.before(oggi)     -> R.drawable.status_circle_red    // in ritardo
                    else                        -> R.drawable.status_circle_green  // ok
                }
            }
        }

        holder.statoAnnaffiatura.background =
            ContextCompat.getDrawable(holder.itemView.context, drawableRes)

        holder.itemView.setOnClickListener { onItemClick(gardenPlant) }
    }

    // Funzione per gestire i giorni per aggiornare lo stato pallino
    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    override fun getItemCount(): Int = gardenPlants.size
}
