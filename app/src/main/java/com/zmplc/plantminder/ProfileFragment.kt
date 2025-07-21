package com.zmplc.plantminder

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zmplc.plantminder.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val user = auth.currentUser
        if (user != null) {
            binding.textNome.text = user.displayName ?: "Nome non disponibile"
            binding.textEmail.text = user.email ?: "Email non disponibile"
            loadNumeroPiante(user.uid)
        } else {
            binding.textNome.text = "Utente non loggato"
            binding.textEmail.text = "-"
            binding.textNumeroPiante.text = "0"
        }

        binding.btnCambioPassword.setOnClickListener {
            if (user != null && !user.email.isNullOrEmpty()) {
                auth.sendPasswordResetEmail(user.email!!)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            showPasswordResetDialog()
                        } else {
                            Toast.makeText(requireContext(),
                                "Errore nell'invio email di reset password",
                                Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(requireContext(),
                    "Utente non loggato o email non disponibile",
                    Toast.LENGTH_LONG).show()
            }
        }

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireContext(), WelcomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        binding.btnEliminaAccount.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Conferma eliminazione account")
                .setMessage("Vuoi davvero eliminare il tuo account? Questa azione è irreversibile.")
                .setPositiveButton("Elimina") { _, _ ->
                    eliminaAccount()
                }
                .setNegativeButton("Annulla", null)
                .show()
        }
    }

    private fun eliminaAccount() {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(requireContext(), "Utente non loggato", Toast.LENGTH_SHORT).show()
            return
        }
        val userId = user.uid
        val gardenRef = firestore.collection("garden").document(userId).collection("userPlants")

        // Prima elimino tutte le piante nel giardino poi elimino il documento relativo al userId
        // Faccio questo perché Firestore non mostra un metodo per eliminare direttamente una collezione
        gardenRef.get().addOnSuccessListener { snapshot ->
            val batch = firestore.batch()
            for (doc in snapshot.documents) {
                batch.delete(doc.reference)
            }

            batch.commit().addOnSuccessListener {
                // Ora elimina il documento garden/{userId}
                firestore.collection("garden").document(userId).delete()
                    .addOnSuccessListener {
                        // Elimino l'utente da Firebase
                        user.delete().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(requireContext(), "Account eliminato", Toast.LENGTH_SHORT).show()
                                val intent = Intent(requireContext(), WelcomeActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            } else {
                                Toast.makeText(requireContext(), "Errore nell'eliminazione account", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Errore nell'eliminazione del giardino", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadNumeroPiante(userId: String) {
        firestore.collection("garden")
            .document(userId)
            .collection("userPlants")
            .get()
            .addOnSuccessListener { documents ->
                val count = documents.size()
                binding.textNumeroPiante.text = count.toString()
            }
            .addOnFailureListener {
                binding.textNumeroPiante.text = "0"
                Toast.makeText(requireContext(), "Errore nel caricamento piante", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showPasswordResetDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Email inviata")
            .setMessage("Ti è stata inviata una email per reimpostare la password. Controlla la tua casella di posta.")
            .setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
            }
                .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
