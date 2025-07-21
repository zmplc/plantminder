package com.zmplc.plantminder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class GardenPlantsAdapter(
    private val gardenPlants: List<GardenPlant>,
    private val onItemClick: (GardenPlant) -> Unit
) : RecyclerView.Adapter<GardenPlantsAdapter.GardenPlantViewHolder>() {

    class GardenPlantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomePersonalizzato: TextView = itemView.findViewById(R.id.nomePersonalizzatoPianta)
        val nomePianta: TextView = itemView.findViewById(R.id.nomePianta)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GardenPlantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_garden_plant, parent, false)
        return GardenPlantViewHolder(view)
    }

    override fun onBindViewHolder(holder: GardenPlantViewHolder, position: Int) {
        val gardenPlant = gardenPlants[position]
        holder.nomePersonalizzato.text = gardenPlant.nomePersonalizzato
        holder.nomePianta.text = gardenPlant.nome

        holder.itemView.setOnClickListener { onItemClick(gardenPlant) }
    }

    override fun getItemCount(): Int = gardenPlants.size
}
