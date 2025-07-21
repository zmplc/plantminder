package com.zmplc.plantminder

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import android.Manifest
import androidx.core.app.ActivityCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import com.zmplc.plantminder.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    val CHANNEL_ID = "plantminder_channel"

    override fun onCreate(savedInstanceState: Bundle?) {
        // Applico il tema
        applySavedTheme()

        WindowCompat.setDecorFitsSystemWindows(window, true)

        Log.d("MainActivity", "onCreate chiamato")

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1
            )
        }
        createNotificationChannel()

        // Ogni 24 ore mando la notifica
        // Da notare che utilizzando PeriodicWorkRequest una notifica viene mandata comunque
        // Quindi avrò current state RUNNING e poi current state ENQUEUED
        val request = PeriodicWorkRequestBuilder<NotificationWorker>(24, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(this).enqueue(request)

        WorkManager.getInstance(this)
            .getWorkInfoByIdLiveData(request.id)
            .observe(this) { workInfo ->
                if (workInfo != null && workInfo.state.isFinished) {
                    if (workInfo.state == androidx.work.WorkInfo.State.SUCCEEDED) {
                        Log.d("MainActivity", "Work completed successfully")
                    } else if (workInfo.state == androidx.work.WorkInfo.State.FAILED) {
                        Log.d("MainActivity", "Work failed")
                    } else {
                        Log.d("MainActivity", "Work finished with state: ${workInfo.state}")
                    }
                } else if (workInfo != null) {
                    Log.d("MainActivity", "Work current state: ${workInfo.state}")
                }
            }


        // Imposto la Toolbar
        setSupportActionBar(binding.plantminderToolbar)

        // Ottiengo il NavHostFragment e il relativo NavController
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    // Con il popBackStack rimuovo tutti i fragment sopra (in questo caso) homeFragment
                    navController.popBackStack(R.id.homeFragment, false)
                    navController.navigate(R.id.homeFragment)
                    true
                }
                R.id.annaffiatureFragment -> {
                    navController.popBackStack(R.id.annaffiatureFragment, false)
                    navController.navigate(R.id.annaffiatureFragment)
                    true
                }
                R.id.plantListFragment -> {
                    navController.popBackStack(R.id.plantListFragment, false)
                    navController.navigate(R.id.plantListFragment)
                    true
                }
                R.id.profileFragment -> {
                    navController.popBackStack(R.id.profileFragment, false)
                    navController.navigate(R.id.profileFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun applySavedTheme() {
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        when (prefs.getString("app_theme", "system")) {
            "light" -> AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    // Opzioni della Toolbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settingsFragment -> {
                findNavController(R.id.nav_host).navigate(R.id.settingsFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun createNotificationChannel() {
        Log.d("MainActivity", "createNotificationChannel() chiamato")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = "Notification Channel"
            val descriptionText = "Channel for plantminder notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            mChannel.description = descriptionText
            // Register the channel with the system. You can't change the importance
            // or other notification behaviors after this.
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as
                    NotificationManager
            notificationManager.createNotificationChannel(mChannel)

            val checkChannel = notificationManager.getNotificationChannel(CHANNEL_ID)
            if (checkChannel != null) {
                Log.d("MainActivity", "Canale notifiche creato: ${checkChannel.name}")
            } else {
                Log.d("MainActivity", "Canale notifiche NON creato")
            }
        }
    }
}