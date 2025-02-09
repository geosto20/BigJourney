package com.example.bigjourney

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.bigjourney.databinding.ActivityAddTripBinding
import com.example.bigjourney.databinding.ActivityMyTripsBinding
import android.app.DatePickerDialog
import android.content.Intent
import android.location.Address
import android.location.Geocoder

import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider

import com.example.bigjourney.model.Trip
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


// AddTripActivity.kt
class AddTripActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddTripBinding
    private lateinit var tripViewModel: TripViewModel
    private lateinit var locationEditText: EditText

    private val getLocationResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val latitude = data?.getDoubleExtra("latitude", 0.0) ?: 0.0
            val longitude = data?.getDoubleExtra("longitude", 0.0) ?: 0.0
            val location = getAddressFromCoordinates(latitude, longitude)

            binding.locationEditText.setText(location)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddTripBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locationEditText = findViewById(R.id.locationEditText)
        val startDateEditText = findViewById<EditText>(R.id.startDateEditText)
        val endDateEditText = findViewById<EditText>(R.id.endDateEditText)
        val submitButton = findViewById<Button>(R.id.submitButton)

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        val startCalendar = Calendar.getInstance()
        val endCalendar = Calendar.getInstance()

        // ✅ Λήψη του userId από το Firebase
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // ✅ Αρχικοποίηση του ViewModel με το userId
        tripViewModel = ViewModelProvider(this, TripViewModelFactory(userId))[TripViewModel::class.java]

        startDateEditText.setOnClickListener {
            DatePickerDialog(this, { _, year, month, dayOfMonth ->
                startCalendar.set(year, month, dayOfMonth)
                startDateEditText.setText(dateFormat.format(startCalendar.time))
            }, startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        endDateEditText.setOnClickListener {
            DatePickerDialog(this, { _, year, month, dayOfMonth ->
                endCalendar.set(year, month, dayOfMonth)
                endDateEditText.setText(dateFormat.format(endCalendar.time))
            }, endCalendar.get(Calendar.YEAR), endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        submitButton.setOnClickListener {
            val location = locationEditText.text.toString().trim()
            val startDateText = startDateEditText.text.toString().trim()
            val endDateText = endDateEditText.text.toString().trim()

            if (location.isEmpty() || startDateText.isEmpty() || endDateText.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                val startDate = dateFormat.parse(startDateText) ?: Date()
                val endDate = dateFormat.parse(endDateText) ?: Date()

                // ✅ Καλούμε το addTrip με το userId
                tripViewModel.addTrip(location, startDate, endDate)

                Toast.makeText(this, "Trip added successfully!", Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(this, "Invalid date format", Toast.LENGTH_SHORT).show()
            }
        }

        binding.locationEditText.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            getLocationResultLauncher.launch(intent)
        }
    }


    fun getAddressFromCoordinates(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(applicationContext, Locale.getDefault())
        val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)

        return if (!addresses.isNullOrEmpty()) {
            val address = addresses[0]
            // Παίρνουμε το όνομα της περιοχής από την Address
            "${address.locality}, ${address.countryName}"
        } else {
            "Address not found"
        }
    }

}