package com.zmplc.plantminder

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class NotificationWorker(
    val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("NotificationWorker", "Esecuzione avvenuta: il worker è stato eseguito")
        return try {
            val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            val userId = prefs.getString("USER_ID", null)

            if (userId == null) {
                Log.d("NotificationWorker", "Nessun userId trovato, esco dal worker")
                return Result.failure()
            }

            val db = FirebaseFirestore.getInstance()
            val snapshot = db.collection("garden")
                .document(userId)
                .collection("userPlants")
                .get()
                .await()

            val pianteDaAnnaffiare = snapshot.documents.mapNotNull { doc ->
                val plant = doc.toObject(GardenPlant::class.java)?.copy(id = doc.id)
                plant?.let {
                    val info = InfoInnaffiatura(it.ultimaInnaffiatura, it.intervalloAnnaffiatura)
                    if (info.daAnnaffiare()) it else null
                }
            }

            pianteDaAnnaffiare.forEachIndexed { index, plant ->
                inviaNotifica(
                    context,
                    plant.nomePersonalizzato.ifEmpty { plant.nome },
                    notificationId = 1000 + index
                )
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("NotificationWorker", "Errore durante l'invio: ${e.message}")
            Result.failure()
        }
    }

    private fun inviaNotifica(context: Context, nomePianta: String, notificationId: Int) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, "plantminder_channel")
            .setSmallIcon(R.drawable.plantminder_logo)
            .setContentTitle("Ricordati di annaffiare \"$nomePianta\"!")
            .setContentText("La pianta \"$nomePianta\" va annaffiata oggi!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        manager.notify(notificationId, notification)
    }
}