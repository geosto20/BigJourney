package com.example.bigjourney

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {
    private lateinit var darkModeSwitch: SwitchCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)


        darkModeSwitch = findViewById(R.id.darkModeSwitch)

        //κατάσταση Dark Mode
        lifecycleScope.launch {
            val isDarkModeEnabled = UserPreferencesManager.darkModePreference(applicationContext).first()

            darkModeSwitch.isChecked = isDarkModeEnabled

            setDarkMode(isDarkModeEnabled)
        }

        // Επεξεργασία αλλαγής του switch
        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                UserPreferencesManager.saveDarkModePreference(applicationContext, isChecked)
                setDarkMode(isChecked)
            }
        }
    }

    // Μέθοδος για την αλλαγή του Dark Mode
    private fun setDarkMode(isDarkModeEnabled: Boolean) {
        val nightMode = if (isDarkModeEnabled) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }
}








