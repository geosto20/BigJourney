package com.example.bigjourney

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.bigjourney.databinding.ActivityMessagesBinding

import com.example.bigjourney.ui.theme.BigJourneyTheme

class MessagesActivity : ComponentActivity() {

    private lateinit var binding: ActivityMessagesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMessagesBinding.inflate(layoutInflater) // Initialize binding
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            val intent = Intent(this, MyTripsActivity::class.java)
            startActivity(intent)
        }

        enableEdgeToEdge()

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