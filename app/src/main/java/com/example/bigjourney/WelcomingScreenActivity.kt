package com.example.bigjourney

import android.os.Bundle
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bigjourney.databinding.ActivityWelcomingScreenBinding

class WelcomingScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomingScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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