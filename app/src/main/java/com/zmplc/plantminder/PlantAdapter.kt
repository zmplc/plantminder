package com.zmplc.plantminder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PlantAdapter(
    private val plantList: List<Plant>,
    private val onItemClick: (Plant) -> Unit
) : RecyclerView.Adapter<PlantAdapter.PlantViewHolder>() {

    class PlantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomePianta: TextView = itemView.findViewById(R.id.nomePianta)
        val nomeScientificoPianta: TextView = itemView.findViewById(R.id.nomeScientificoPianta)
        val iconaFreccia: ImageView = itemView.findViewById(R.id.iconaFreccia)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_plant, parent, false)
        return PlantViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        val plant = plantList[position]
        holder.nomePianta.text = plant.nome
        holder.nomeScientificoPianta.text = plant.nomeScientifico

        holder.itemView.setOnClickListener { onItemClick(plant) }
        holder.iconaFreccia.setOnClickListener { onItemClick(plant) }
    }

    override fun getItemCount(): Int = plantList.size
}