package com.zmplc.plantminder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class PlantListViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _plants = MutableLiveData<List<Plant>>()
    val plants: LiveData<List<Plant>> = _plants

    init {
        fetchPlantsFromFirestore()
    }

    fun setFilter(filter: String?) {
        if (filter.isNullOrEmpty()) {
            fetchPlantsFromFirestore()
        } else {
            fetchPlantsByType(filter)
        }
    }

    private fun fetchPlantsFromFirestore() {
        db.collection("plants")
            .get()
            .addOnSuccessListener { result ->
                val list = result.map { doc ->
                    Plant(
                        id = doc.getString("id") ?: doc.id,
                        nome = doc.getString("nome") ?: "",
                        nomeScientifico = doc.getString("nomeScientifico") ?: "",
                        descrizione = doc.getString("descrizione") ?: "",
                        tipo = doc.getString("tipo") ?: "",
                        temperatura = doc.getString("temperatura") ?: "",
                        luce = doc.getString("luce") ?: "",
                        acqua = doc.getString("acqua") ?: "",
                        intervalloAnnaffiatura = doc.getLong("intervalloAnnaffiatura")?.toInt() ?: 0
                    )
                }
                _plants.value = list
            }
    }

    private fun fetchPlantsByType(type: String) {
        db.collection("plants")
            .whereEqualTo("tipo", type)
            .get()
            .addOnSuccessListener { result ->
                val list = result.map { doc ->
                    Plant(
                        id = doc.getString("id") ?: doc.id,
                        nome = doc.getString("nome") ?: "",
                        nomeScientifico = doc.getString("nomeScientifico") ?: "",
                        descrizione = doc.getString("descrizione") ?: "",
                        tipo = doc.getString("tipo") ?: "",
                        temperatura = doc.getString("temperatura") ?: "",
                        luce = doc.getString("luce") ?: "",
                        acqua = doc.getString("acqua") ?: "",
                        intervalloAnnaffiatura = doc.getLong("intervalloAnnaffiatura")?.toInt() ?: 0
                    )
                }
                _plants.value = list
            }
    }
}