package com.example.bigjourney

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
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bigjourney.MainApplication.Companion.tripDatabase
import com.example.bigjourney.adapters.TripAdapter
import com.example.bigjourney.database.TripDao
import com.example.bigjourney.model.Trip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

import com.google.firebase.storage.FirebaseStorage

class MyTripsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyTripsBinding
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var tripViewModel: TripViewModel
    private lateinit var tripAdapter: TripAdapter
    private lateinit var deleteButton: Button
    private lateinit var toggle: ActionBarDrawerToggle
    private val trips = mutableListOf<Trip>()
    private var selectedTrips = mutableSetOf<Trip>()
    private lateinit var photoUri: Uri
    private val REQUEST_CAMERA_PERMISSION = 2

    private lateinit var tripDao: TripDao
    private lateinit var tripsNotification: TripsNotification

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyTripsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val profileNotificationHelper = ProfileNotificationHelper(this)
        profileNotificationHelper.checkUserProfile(this)


        tripDao = tripDatabase.getTripDao()
        tripsNotification = TripsNotification(this, tripDao)

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let {
            tripsNotification.checkUserTripsAndNotify(it)  // Ελέγχουμε αν ο χρήστης έχει ταξίδια
        }


        // Βρίσκουμε τα στοιχεία στο layout
        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById(R.id.navigationView)

        // Συνδέουμε το Navigation Drawer με το header
        val headerView = navView.getHeaderView(0)
        val userNameTextView = headerView.findViewById<TextView>(R.id.user_name)
        val userEmailTextView = headerView.findViewById<TextView>(R.id.user_email)
        val profileImageView = headerView.findViewById<ImageView>(R.id.profile_image)


        if (userId == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }


        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        user?.let {
            userNameTextView.text = it.displayName ?: "No Name"
            userEmailTextView.text = it.email ?: "No Email"


            it.photoUrl?.let { uri ->
                Glide.with(this).load(uri).into(profileImageView)
            }
        }

        // Toolbar & Navigation Drawer
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "BigJourney"
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.black))

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        toggle.drawerArrowDrawable.color = Color.BLACK
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Χειρισμός επιλογών μενού
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                }
                R.id.menu_logout -> {
                    logoutUser()
                }

            }
            true
        }

        // RecyclerView για τα ταξίδια
        val recyclerView: RecyclerView = findViewById(R.id.tripsRecyclerView)
        deleteButton = findViewById(R.id.deleteButton)
        tripViewModel = ViewModelProvider(this, TripViewModelFactory(userId))[TripViewModel::class.java]
        recyclerView.layoutManager = LinearLayoutManager(this)

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
                    deleteButton.visibility = if (selectedTrips.isEmpty()) View.GONE else View.VISIBLE
                }
                recyclerView.adapter = tripAdapter
            }
        }

        deleteButton.setOnClickListener {
            tripViewModel.deleteTrips(selectedTrips.toList())
            selectedTrips.clear()
            tripAdapter.clearSelections()
            deleteButton.visibility = View.GONE
        }

        val profileButton: ImageButton = findViewById(R.id.profileIcon)
        profileButton.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }



        val cameraButton: ImageButton = findViewById(R.id.cameraIcon)
        cameraButton.setOnClickListener {
            checkCameraPermission()
        }

        val chatButton: ImageButton = findViewById(R.id.chatIcon)
        chatButton.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            startActivity(intent)
        }

        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                uploadImageToFirebase(photoUri)
            }
        }


        binding.chatIcon.setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }

        binding.cameraCollectionIcon.setOnClickListener {
            startActivity(Intent(this, ImagesActivity::class.java))
        }

        binding.addIcon.setOnClickListener {
            startActivity(Intent(this, AddTripActivity::class.java))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logoutUser() {

        FirebaseAuth.getInstance().signOut()

        val intent = Intent(this, SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

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


    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            uploadImageToFirebase(photoUri)
        } else {
            Toast.makeText(this, "Image capture failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openCamera() {
        try {
            val photoFile = createImageFile()
            photoUri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", photoFile)

            takePictureLauncher.launch(photoUri)
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




