package com.example.bigjourney

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.room.Room
import com.example.bigjourney.database.MIGRATION_1_2
import com.example.bigjourney.database.MIGRATION_2_1
import com.example.bigjourney.database.TripDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainApplication : Application() {
    companion object {
        val tripDatabase: TripDatabase by lazy {
            Room.databaseBuilder(
                instance.applicationContext,
                TripDatabase::class.java,
                TripDatabase.NAME
            )
                .fallbackToDestructiveMigration()
                .build()
        }

        lateinit var instance: MainApplication
            private set
    }

    // Δημιουργία ενός custom CoroutineScope με SupervisorJob
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        instance = this

        /*// Χρήση CoroutineScope για async λειτουργίες
        GlobalScope.launch(Dispatchers.Main) {
            val userPreferencesManager = UserPreferencesManager(this@MainApplication)
            val isDarkModeEnabled = userPreferencesManager.darkModeEnabled.first()

            // Ενημέρωση του Dark Mode όταν ξεκινά η εφαρμογή
            AppCompatDelegate.setDefaultNightMode(
                if (isDarkModeEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )

            // Toggle Dark Mode (Απενεργοποιεί το Dark Mode αν είναι ενεργό ή το ενεργοποιεί αν είναι ανενεργό)
            userPreferencesManager.toggleDarkMode()

            // Ενημέρωση του Dark Mode στην εφαρμογή μετά το Toggle
            val newDarkModeStatus = userPreferencesManager.darkModeEnabled.first()
            AppCompatDelegate.setDefaultNightMode(
                if (newDarkModeStatus) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }*/
    }

}


