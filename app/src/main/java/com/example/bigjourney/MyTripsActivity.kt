package com.example.bigjourney

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.drawerlayout.widget.DrawerLayout
import com.example.bigjourney.databinding.ActivityMyTripsBinding
import com.google.android.material.navigation.NavigationView
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.Manifest
import android.graphics.Color
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bigjourney.adapters.TripAdapter
import com.example.bigjourney.model.Trip
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.storage.FirebaseStorage

class MyTripsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyTripsBinding
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var tripViewModel: TripViewModel
    private lateinit var tripAdapter: TripAdapter
    private lateinit var deleteButton: Button

    private lateinit var toggle: ActionBarDrawerToggle


    private val trips = mutableListOf<Trip>() // Θα κρατάμε τις εγγραφές ταξιδιών
    private var selectedTrips = mutableSetOf<Trip>() // Επιλεγμένα ταξίδια για διαγραφή
    private val REQUEST_CAMERA_PERMISSION = 2
    private lateinit var photoUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyTripsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recyclerView: RecyclerView = findViewById(R.id.tripsRecyclerView)
        deleteButton = findViewById(R.id.deleteButton)

        // Αρχικοποίηση ViewModel
        tripViewModel = ViewModelProvider(this)[TripViewModel::class.java]
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Βρες το TextView
        val noTripsTextView: TextView = findViewById(R.id.noTripsTextView)
        val noTripsLayout: View = findViewById(R.id.noTripsLayout)
        val tripsLayout: View = findViewById(R.id.tripsLayout)


        tripViewModel.tripList.observe(this) { trips ->
            if (trips.isEmpty()) {
                noTripsLayout.visibility = View.VISIBLE
                tripsLayout.visibility = View.GONE
                noTripsTextView.visibility = View.VISIBLE

            } else {

                noTripsLayout.visibility = View.GONE
                tripsLayout.visibility = View.VISIBLE
                noTripsTextView.visibility = View.GONE
                this.trips.clear()
                this.trips.addAll(trips)

                tripAdapter = TripAdapter(trips) { selectedList ->
                    selectedTrips = selectedList.toMutableSet()
                    // Εμφάνιση κουμπιού διαγραφής αν υπάρχουν επιλεγμένα ταξίδια
                    deleteButton.visibility = if (selectedTrips.isEmpty()) View.GONE else View.VISIBLE
                }
                recyclerView.adapter = tripAdapter
            }
        }

        deleteButton.setOnClickListener {
            tripViewModel.deleteTrips(selectedTrips.toList())
            selectedTrips.clear() // Καθαρισμός της λίστας επιλεγμένων
            tripAdapter.clearSelections()
            deleteButton.visibility = View.GONE
        }

        val cameraButton: ImageButton = findViewById(R.id.cameraIcon)
        cameraButton.setOnClickListener {
            checkCameraPermission()
        }

        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                uploadImageToFirebase(photoUri)
            }
        }

        binding.chatIcon.setOnClickListener {
            val intent = Intent(this, MessagesActivity::class.java)
            startActivity(intent)
        }

        binding.cameraCollectionIcon.setOnClickListener {
            val intent = Intent(this, ImagesActivity::class.java)
            startActivity(intent)
        }

        binding.addIcon.setOnClickListener {
            val intent = Intent(this, AddTripActivity::class.java)
            startActivity(intent)
        }

        // Συνδέουμε τα views
        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById(R.id.navigationView)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Αλλαγη του τίτλου
        supportActionBar?.title = "BigJourney"
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.black))

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)

        // Αλλαγή χρώματος στο hamburger icon σε μαύρο
        toggle.drawerArrowDrawable.color = Color.BLACK

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Χειρισμός clicks στο μενού
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_settings -> {
                    Toast.makeText(this, "Yamete kudasai", Toast.LENGTH_SHORT).show()
                }
                R.id.menu_logout -> {
                    logoutUser()
                }
            }
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logoutUser() {

        FirebaseAuth.getInstance().signOut() // Αποσύνδεση από το Firebase

        val intent = Intent(this, SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Καθαρισμός του stack
        startActivity(intent)
        // Δείξε μήνυμα ή κάνε logout
        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
    }


    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    private fun checkCameraPermission() {
        Toast.makeText(this, "Checking camera permission", Toast.LENGTH_SHORT).show()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        } else {
            Toast.makeText(this, "Permission granted, opening camera", Toast.LENGTH_SHORT).show()
            openCamera()
        }
    }

    // ✅ Ενημερωμένο `takePictureLauncher` με Firebase Upload
    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            uploadImageToFirebase(photoUri) // ✅ Ανέβασμα στο Firebase
        } else {
            Toast.makeText(this, "Image capture failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openCamera() {
        try {
            val photoFile = createImageFile()
            photoUri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", photoFile)

            takePictureLauncher.launch(photoUri) // ✅ Χρησιμοποιούμε το Uri
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to open camera: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }




    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
        }
    }



    // ✅ Βελτιωμένο `uploadImageToFirebase`
    private fun uploadImageToFirebase(fileUri: Uri) {
        val storageRef = FirebaseStorage.getInstance().reference
        val fileRef = storageRef.child("images/${System.currentTimeMillis()}.jpg")

        fileRef.putFile(fileUri)
            .addOnSuccessListener {
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    Log.d("Firebase", "Image URL: $uri")
                    Toast.makeText(this, "Upload successful!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}




