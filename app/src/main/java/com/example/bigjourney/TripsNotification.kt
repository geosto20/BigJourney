package com.example.bigjourney

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.bigjourney.database.TripDao
import android.Manifest


class TripsNotification(private val context: Context, private val tripDao: TripDao) {

    private val CHANNEL_ID = "TRIPS_CHANNEL"
    private val NOTIFICATION_ID = 1

    // Δημιουργία του notification channel για Android 8+ (Oreo και πάνω)
    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val name = "Trips Notification Channel"
            val descriptionText = "Channel for trip notifications"
            val importance = android.app.NotificationManager.IMPORTANCE_HIGH
            val channel = android.app.NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Εμφάνιση ειδοποίησης όταν δεν υπάρχουν προγραμματισμένα ταξίδια
    fun showAddTripNotification() {
        // Έλεγχος αν έχουμε άδεια για να στείλουμε ειδοποιήσεις
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            // Δημιουργία του notification channel (Αν δεν έχει δημιουργηθεί ήδη)
            createNotificationChannel()

            // Δημιουργία του Intent που θα ανοίξει το AddTripActivity όταν πατηθεί η ειδοποίηση
            val intent = Intent(context, AddTripActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Δημιουργία του notification
            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("No Upcoming Trips")
                .setContentText("You don't have any upcoming trips. Tap to add one!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true) // Η ειδοποίηση θα κλείσει όταν την πατήσει ο χρήστης
                .setContentIntent(pendingIntent) // Όταν πατηθεί η ειδοποίηση, θα ανοίξει το AddTripActivity
                .build()

            // Εμφάνιση του notification
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(NOTIFICATION_ID, notification)
        } else {
            // Αν δεν έχουμε την άδεια, ζητάμε την άδεια
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Για Android 13 και πάνω
                ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 0)
            }
        }
    }




    // Έλεγχος αν υπάρχουν ταξίδια για τον χρήστη και εμφάνιση ειδοποίησης αν δεν υπάρχουν
    fun checkUserTripsAndNotify(userId: String) {
        // Χρησιμοποιούμε το LiveData για να παρακολουθήσουμε το count των ταξιδιών
        tripDao.getUserTripCount(userId).observeForever { tripCount ->
            if (tripCount == 0) {
                // Αφαιρούμε το userId καθώς δεν χρειάζεται για την ειδοποίηση
                showAddTripNotification()
            }
        }
    }

}

