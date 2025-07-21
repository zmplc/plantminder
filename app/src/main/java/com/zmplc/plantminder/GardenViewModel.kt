package com.zmplc.plantminder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import java.util.Date

class GardenViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _gardenPlants = MutableLiveData<List<GardenPlant>>()
    val gardenPlants: LiveData<List<GardenPlant>> = _gardenPlants

    private var listener: ListenerRegistration? = null

    init {
        fetchUserGardenPlants()
    }

    fun fetchUserGardenPlants() {
        val userId = auth.currentUser?.uid ?: return

        listener?.remove()

        listener = db.collection("garden")
            .document(userId)
            .collection("userPlants")
            .addSnapshotListener { snapshots, e ->
                if (e != null) return@addSnapshotListener

                if (snapshots != null) {
                    val list = snapshots.map { doc ->
                        val timestamp = doc.getTimestamp("ultimaInnaffiatura")
                        val ultimaInnaffiaturaDate: Date? = timestamp?.toDate()


                        GardenPlant(
                            id = doc.getString("id") ?: doc.id,
                            nomePersonalizzato = doc.getString("nomePersonalizzato") ?: "",
                            nome = doc.getString("nome") ?: "",
                            posizione = doc.getString("posizione") ?: "",
                            intervalloAnnaffiatura = doc.getLong("intervalloAnnaffiatura")?.toInt() ?: 0,
                            ultimaInnaffiatura = ultimaInnaffiaturaDate
                        )
                    }
                    _gardenPlants.value = list
                }
            }
    }

    override fun onCleared() {
        // Per evitare memory leaks quando tolgo il viewmodel tolgo anche il listener (preso da documentazione)
        super.onCleared()
        listener?.remove()
    }
}