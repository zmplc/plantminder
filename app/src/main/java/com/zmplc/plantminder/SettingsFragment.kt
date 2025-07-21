package com.zmplc.plantminder

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.content.Context
import androidx.core.net.toUri
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.core.content.edit
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.zmplc.plantminder.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)

        setupSwitches() // Switch tema
        setupActions()

        return binding.root
    }

    private fun setupSwitches() {
        // Switch Tema
        val prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
        val savedTheme = prefs.getString("app_theme", "system")

        // Imposto switch in base al tema salvato
        binding.switchTema.isChecked = savedTheme == "dark"

        binding.switchTema.setOnCheckedChangeListener { _, isChecked ->
            val newTheme = if (isChecked) "dark" else "light"

            prefs.edit {
                putString("app_theme", newTheme)
            }

            AppCompatDelegate.setDefaultNightMode(
                when (newTheme) {
                    "dark" -> AppCompatDelegate.MODE_NIGHT_YES
                    "light" -> AppCompatDelegate.MODE_NIGHT_NO
                    else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
            )
        }
    }

    private fun setupActions() {
        // Dialog per conferma elimina piante
        binding.btnCancellaPiante.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Conferma eliminazione")
                .setMessage("Vuoi davvero eliminare tutte le piante dal tuo giardino?")
                .setPositiveButton("Si") { _, _ -> cancellaPiante() }
                .setNegativeButton("Annulla", null)
                .show()
        }

        // Apri documentazione (README github)
        binding.btnDocumentazione.setOnClickListener {
            val url = "https://github.com/zmplc/plantminder/blob/main/README.md"
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            startActivity(intent)
        }
    }

    // Funzione per eliminare le piante dal giardino dell'utente -> garden/{userId}/userPlants/{pianta}
    private fun cancellaPiante() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val db = FirebaseFirestore.getInstance()
            val userPlantsRef = db.collection("garden").document(userId).collection("userPlants")

            userPlantsRef.get().addOnSuccessListener { snapshot ->
                val batch = db.batch()
                snapshot.documents.forEach { doc ->
                    batch.delete(doc.reference)
                }
                batch.commit().addOnSuccessListener {
                    Toast.makeText(requireContext(), "Tutte le piante sono state eliminate", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Errore durante l'eliminazione", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
