package com.zmplc.plantminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale
import com.zmplc.plantminder.databinding.FragmentGardenPlantDetailBinding

class GardenPlantDetailFragment : Fragment() {

    private lateinit var binding: FragmentGardenPlantDetailBinding

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val args: GardenPlantDetailFragmentArgs by navArgs()
    private lateinit var gardenPlant: GardenPlant

    private var deletedPlantData: GardenPlant? = null

    private val dateFormatter = SimpleDateFormat("dd MMMM yyyy", Locale.ITALIAN)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGardenPlantDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gardenPlant = args.gardenPlant
        binding.nomePiantaTextView.text = gardenPlant.nomePersonalizzato.ifEmpty { gardenPlant.nome }

        val info = InfoInnaffiatura(
            ultimaInnaffiatura = gardenPlant.ultimaInnaffiatura,
            intervalloAnnaffiatura = gardenPlant.intervalloAnnaffiatura
        )

        // Aggiorno lo stato dell'innaffiatura (testo+pallino)
        aggiornaStatoAnnaffiatura(info)

        binding.btnAnnaffiata.setOnClickListener {
            segnaComeAnnaffiata()
        }

        binding.btnElimina.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        binding.btnRinomina.setOnClickListener {
            showRenameDialog()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            isEnabled = false
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun aggiornaStatoAnnaffiatura(info: InfoInnaffiatura) {
        val giorni = info.giorniDallUltimaAnnaffiatura()
        val statoView = binding.statoAnnaffiatura
        val statoText = binding.statoAnnaffiaturaTextView

        val prossima = info.prossimaAnnaffiatura()
        val prossimaAnnaffiatura = if (prossima != null) dateFormatter.format(prossima) else "Non disponibile"

        when {
            giorni < 0 -> { // pianta mai annaffiata
                statoView.setBackgroundResource(R.drawable.status_circle)
                statoText.text = "Non annaffiata"
            }
            info.daAnnaffiare() -> {
                val ritardo = giorni - info.intervalloAnnaffiatura
                if (ritardo > 0) {
                    statoView.setBackgroundResource(R.drawable.status_circle_red)
                    statoText.text = "Annaffiatura in ritardo di $ritardo giorni"
                } else {
                    statoView.setBackgroundResource(R.drawable.status_circle_blue)
                    statoText.text = "Da annaffiare oggi"
                }
            }
            else -> {
                statoView.setBackgroundResource(R.drawable.status_circle_green)
                statoText.text = "Annaffiata"
            }
        }

        binding.prossimaAnnaffiaturaTextView.text = "Prossima annaffiatura: \n$prossimaAnnaffiatura"
    }



    private fun segnaComeAnnaffiata() {
        val userId = auth.currentUser?.uid ?: return
        val plantId = gardenPlant.id
        val now = Date()
        val nowTimestamp = com.google.firebase.Timestamp(now)

        db.collection("garden")
            .document(userId)
            .collection("userPlants")
            .document(plantId)
            .update("ultimaInnaffiatura", nowTimestamp)
            .addOnSuccessListener {
                val info = InfoInnaffiatura(
                    ultimaInnaffiatura = now,
                    intervalloAnnaffiatura = gardenPlant.intervalloAnnaffiatura
                )
                aggiornaStatoAnnaffiatura(info)

                // Mostra dialog di conferma
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Annaffiatura registrata")
                    .setMessage("Hai segnato la pianta come annaffiata oggi.")
                    .setPositiveButton("Ok") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Errore durante l'aggiornamento", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showDeleteConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Conferma eliminazione")
            .setMessage("Sei sicuro di voler eliminare questa pianta?")
            .setPositiveButton("Elimina") { _, _ ->
                deletePlantWithUndo()
            }
            .setNegativeButton("Annulla", null)
            .show()
    }

    private fun deletePlantWithUndo() {
        val userId = auth.currentUser?.uid ?: return
        val plantId = gardenPlant.id

        deletedPlantData = gardenPlant

        db.collection("garden")
            .document(userId)
            .collection("userPlants")
            .document(plantId)
            .delete()
            .addOnSuccessListener {
                showUndoSnackbar(userId, plantId)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Errore durante l'eliminazione", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showUndoSnackbar(userId: String, plantId: String) {
        Snackbar.make(binding.root, "Pianta eliminata", Snackbar.LENGTH_LONG)
            .setAction("ANNULLA") {
                deletedPlantData?.let { plant ->
                    db.collection("garden")
                        .document(userId)
                        .collection("userPlants")
                        .document(plantId)
                        .set(plant)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Eliminazione annullata", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Errore nel ripristino", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addCallback(object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    if (event != DISMISS_EVENT_ACTION) {
                        findNavController().popBackStack(R.id.homeFragment, false)
                    }
                }
            })
            .show()
    }

    private fun showRenameDialog() {
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.dialog_rename, null, false)

        val editText = dialogView.findViewById<EditText>(R.id.nuovoNome)
        editText.setText(gardenPlant.nomePersonalizzato)
        editText.setSelection(editText.text.length)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Rinomina pianta")
            .setView(dialogView)
            .setNegativeButton("Annulla", null)
            .setPositiveButton("Salva") { _, _ ->
                val nuovoNome = editText.text.toString().trim()
                if (nuovoNome.isNotEmpty()) {
                    renamePlant(nuovoNome)
                } else {
                    Toast.makeText(requireContext(), "Inserisci un nome valido", Toast.LENGTH_SHORT).show()
                }
            }
            .create()

        dialog.show()
    }


    private fun renamePlant(nuovoNome: String) {
        val userId = auth.currentUser?.uid ?: return
        val plantId = gardenPlant.id

        db.collection("garden")
            .document(userId)
            .collection("userPlants")
            .document(plantId)
            .update("nomePersonalizzato", nuovoNome)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Nome aggiornato", Toast.LENGTH_SHORT).show()
                binding.nomePiantaTextView.text = nuovoNome
                gardenPlant = gardenPlant.copy(nomePersonalizzato = nuovoNome)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Errore durante l'aggiornamento", Toast.LENGTH_SHORT).show()
            }
    }
}