package com.zmplc.plantminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.LinearLayout
import android.widget.RadioGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.content.DialogInterface
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.core.view.isVisible
import java.util.Date
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.zmplc.plantminder.databinding.FragmentPlantDetailBinding

class PlantDetailFragment : Fragment() {

    private var _binding: FragmentPlantDetailBinding? = null
    private val binding get() = _binding!!

    val args: PlantDetailFragmentArgs by navArgs()

    lateinit var pianta: Plant

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlantDetailBinding.inflate(inflater, container, false)

        binding.backArrow.setOnClickListener {
            findNavController().navigate(R.id.action_plantDetailFragment_to_plantListFragment)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pianta = args.plant

        binding.nomePiantaTextView.text = pianta.nome
        binding.nomeScientificoTextView.text = pianta.nomeScientifico
        binding.descrizionePiantaTextView.text = pianta.descrizione
        binding.temperaturaTextView.text = pianta.temperatura
        binding.luceTextView.text = pianta.luce
        binding.acquaTextView.text = pianta.acqua

        binding.aggiungiAlGiardinoBottone.setOnClickListener {
            mostraDialogAggiungiPianta(pianta)
        }
    }

    private fun mostraDialogAggiungiPianta(pianta: Plant) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_aggiungi_pianta, null)
        val stepPosizione = dialogView.findViewById<LinearLayout>(R.id.stepPosizione)
        val stepIntervalloAnnaffiatura = dialogView.findViewById<LinearLayout>(R.id.stepIntervalloAnnaffiatura)
        val stepNome = dialogView.findViewById<LinearLayout>(R.id.stepNome)

        val radioGroupPosizione = dialogView.findViewById<RadioGroup>(R.id.radioGroupPosizione)
        val radioGroupAnnaffiatura = dialogView.findViewById<RadioGroup>(R.id.radioGroupAnnaffiatura)
        val radioIntervalloDefault = dialogView.findViewById<com.google.android.material.radiobutton.MaterialRadioButton>(R.id.radioIntervalloDefault)

        val inputLayoutNome = dialogView.findViewById<TextInputLayout>(R.id.inputLayoutNome)
        val editTextNome = dialogView.findViewById<TextInputEditText>(R.id.nomePersonalizzatoEditText)

        val inputLayoutIntervalloPersonalizzato = dialogView.findViewById<TextInputLayout>(R.id.inputLayoutIntervalloPersonalizzato)
        val editTextIntervalloPersonalizzato = dialogView.findViewById<TextInputEditText>(R.id.intervalloPersonalizzatoEditText)

        var posizioneScelta: String? = null
        var intervalloAnnaffiatura: Int? = null

        val intervalloDefault = pianta.intervalloAnnaffiatura
        radioIntervalloDefault.text = "Consigliato: ogni $intervalloDefault giorni"

        radioGroupAnnaffiatura.setOnCheckedChangeListener { _, checkedId ->
            inputLayoutIntervalloPersonalizzato.visibility = if (checkedId == R.id.radioIntervalloPersonalizzato) View.VISIBLE else View.GONE
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Aggiungi al giardino")
            .setView(dialogView)
            .setNegativeButton("Annulla", null)
            .setPositiveButton("Avanti", null)
            .create()

        dialog.setOnShowListener {
            val btnAvanti = dialog.getButton(DialogInterface.BUTTON_POSITIVE)

            btnAvanti.setOnClickListener {
                when {
                    stepPosizione.isVisible -> {
                        val selectedId = radioGroupPosizione.checkedRadioButtonId
                        if (selectedId == -1) {
                            Toast.makeText(requireContext(), "Seleziona una posizione", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }
                        posizioneScelta = when (selectedId) {
                            R.id.radioInCasa -> "In vaso, in casa"
                            R.id.radioFuori -> "In vaso, esterno"
                            R.id.radioGiardino -> "Giardino, in terra"
                            R.id.radioOrto -> "Orto (verdure)"
                            else -> null
                        }
                        stepPosizione.visibility = View.GONE
                        stepIntervalloAnnaffiatura.visibility = View.VISIBLE
                    }

                    stepIntervalloAnnaffiatura.isVisible -> {
                        val selectedId = radioGroupAnnaffiatura.checkedRadioButtonId
                        if (selectedId == -1) {
                            Toast.makeText(requireContext(), "Seleziona un intervallo di annaffiatura", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }

                        intervalloAnnaffiatura = if (selectedId == R.id.radioIntervalloDefault) {
                            intervalloDefault
                        } else {
                            val input = editTextIntervalloPersonalizzato.text.toString()
                            val customValue = input.toIntOrNull()
                            if (customValue == null || customValue <= 0) {
                                inputLayoutIntervalloPersonalizzato.error = "Inserisci un numero valido"
                                return@setOnClickListener
                            }
                            inputLayoutIntervalloPersonalizzato.error = null
                            customValue
                        }

                        stepIntervalloAnnaffiatura.visibility = View.GONE
                        stepNome.visibility = View.VISIBLE
                        btnAvanti.text = "Aggiungi"
                    }

                    stepNome.isVisible -> {
                        val nomePersonalizzato = editTextNome.text.toString().trim()
                        if (nomePersonalizzato.isEmpty()) {
                            inputLayoutNome.error = "Inserisci un nome valido"
                            return@setOnClickListener
                        }
                        inputLayoutNome.error = null

                        val userId = FirebaseAuth.getInstance().currentUser?.uid
                        if (userId != null && posizioneScelta != null && intervalloAnnaffiatura != null) {
                            aggiungiPiantaAlGiardino(userId, pianta, nomePersonalizzato, posizioneScelta!!, intervalloAnnaffiatura!!)
                            dialog.dismiss()
                        } else {
                            Toast.makeText(requireContext(), "Errore: dati mancanti", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        dialog.show()
    }

    private fun aggiungiPiantaAlGiardino(userId: String, pianta: Plant, nomePersonalizzato: String, posizione: String, intervalloAnnaffiatura: Int) {
        val db = FirebaseFirestore.getInstance()
        val gardenPlantData = hashMapOf(
            "nome" to pianta.nome,
            "nomePersonalizzato" to nomePersonalizzato,
            "posizione" to posizione,
            "intervalloAnnaffiatura" to intervalloAnnaffiatura,
            "ultimaInnaffiatura" to null,
            "aggiuntaIl" to FieldValue.serverTimestamp()
        )
        db.collection("garden")
            .document(userId)
            .collection("userPlants")
            .document()
            .set(gardenPlantData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "$nomePersonalizzato aggiunta al giardino", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Errore: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
