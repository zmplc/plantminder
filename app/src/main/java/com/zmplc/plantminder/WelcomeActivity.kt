package com.zmplc.plantminder

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.core.content.edit
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth

class WelcomeActivity : AppCompatActivity() {

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->
        this.onSignInResult(res)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val user = FirebaseAuth.getInstance().currentUser

        if (result.resultCode == RESULT_OK && user != null) {
            // Se login ok -> verifico se l'email è verificata
            if (user.isEmailVerified) {
                // Salvo l'userId nelle SharedPreferences
                val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
                prefs.edit {
                    putString("USER_ID", user.uid)
                }
                android.util.Log.d("WelcomeActivity", "UserID salvato: ${user.uid}")
                // Se l'email è verificata -> MainActivity
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("USER_EMAIL", user.email)
                startActivity(intent)
                finish()
            } else {
                // Se non è verificata viene inviata email di verifica all'utente
                user.sendEmailVerification()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                this, "Email di verifica inviata! Controlla la tua email.", Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                this, "Errore nell'invio della email di verifica.", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                // Se login effettuato senza aver verificato l'email -> avviso verifica email
                Toast.makeText(this, "Verifica l'email per accedere.", Toast.LENGTH_LONG).show()
                FirebaseAuth.getInstance().signOut()  // Log out the user
            }
        } else {
            // Errore nell'autenticazione
            Toast.makeText(this, "Errore durante l'autenticazione.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun signIn(){
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setLogo(R.drawable.plantminder_logo)
            .setTheme(R.style.Theme_Plantminder)
            .build()
        signInLauncher.launch(signInIntent)
    }

    private fun applySavedTheme() {
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        when (prefs.getString("app_theme", "system")) {
            "light" -> AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Applico il tema
        applySavedTheme()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        findViewById<Button>(R.id.iniziaButton).setOnClickListener {
            signIn()
        }
    }
}
