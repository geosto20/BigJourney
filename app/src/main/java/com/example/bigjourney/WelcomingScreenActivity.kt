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
            // Ανάκτηση της αποθηκευμένης προτίμησης dark mode (προεπιλεγμένο το "false" αν δεν υπάρχει)
            val isDarkModeEnabled = UserPreferencesManager.darkModePreference(applicationContext).first()

            // Ορισμός του theme σύμφωνα με την προτίμηση
            setDarkMode(isDarkModeEnabled)
        }

        // Συνδέουμε το layout μέσω του View Binding
        binding = ActivityWelcomingScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ορισμός ενεργειών για τα κουμπιά
        binding.button1.setOnClickListener {
            // Ενέργεια για το κουμπί Sign In
            Toast.makeText(this, "Sign In clicked", Toast.LENGTH_SHORT).show()
            // Παράδειγμα πλοήγησης σε άλλη οθόνη
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        binding.button2.setOnClickListener {
            // Ενέργεια για το κουμπί Sign Up
            Toast.makeText(this, "Sign Up clicked", Toast.LENGTH_SHORT).show()
            // Παράδειγμα πλοήγησης σε άλλη οθόνη
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


/*@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BigJourneyTheme {
        Greeting("Android")
    }
}*/