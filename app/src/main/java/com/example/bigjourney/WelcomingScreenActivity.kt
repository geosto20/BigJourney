package com.example.bigjourney

import android.os.Bundle
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.example.bigjourney.databinding.ActivityWelcomingScreenBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class WelcomingScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomingScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Έλεγχος του dark mode προτιμήσεων όταν η εφαρμογή ξεκινάει
        lifecycleScope.launch {
            // Ανάκτηση της αποθηκευμένης προτίμησης dark mode
            val isDarkModeEnabled = UserPreferencesManager.darkModePreference(applicationContext).first()


            setDarkMode(isDarkModeEnabled)
        }


        binding = ActivityWelcomingScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ορισμός ενεργειών για τα κουμπιά
        binding.button1.setOnClickListener {

            Toast.makeText(this, "Sign In clicked", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        binding.button2.setOnClickListener {

            Toast.makeText(this, "Sign Up clicked", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
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
