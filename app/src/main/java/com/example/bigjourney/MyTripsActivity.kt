package com.example.bigjourney

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
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
import android.net.http.HttpResponseCache.install
import android.util.Log
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.GravityCompat
import com.google.firebase.storage.FirebaseStorage





class MyTripsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyTripsBinding
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>

    private lateinit var toggle: ActionBarDrawerToggle

    private val REQUEST_CAMERA_PERMISSION = 2
    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var photoUri: Uri




    // Register the ActivityResultLauncher to take a picture
    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            val bitmap = BitmapFactory.decodeFile(photoUri.path)
            findViewById<ImageView>(R.id.imageView).setImageBitmap(bitmap)
        } else {
            Toast.makeText(this, "Image capture failed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyTripsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.activity_my_trips)

        val cameraButton: ImageButton = findViewById(R.id.cameraIcon)
        cameraButton.setOnClickListener {
            checkCameraPermission()
        }


        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                uploadImageToFirebase(photoUri)
            }
        }

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        //Αλλαγη του τίτλου
        supportActionBar?.title = "BigJourney"
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.black))


         binding.chatIcon.setOnClickListener {
            val intent = Intent(this, MessagesActivity::class.java)
            startActivity(intent)
        }

        binding.cameraCollectionIcon.setOnClickListener {
            val intent = Intent(this, ImagesActivity::class.java)
            startActivity(intent)
        }


        // Συνδέουμε τα views
        val drawerLayout : DrawerLayout = findViewById(R.id.drawerLayout)
        val navView : NavigationView = findViewById(R.id.navigationView)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)

        // Αλλαγή χρώματος στο hamburger icon σε μαύρο
        toggle.drawerArrowDrawable.color = Color.BLACK

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        /*onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    remove()  // 🔹 Σιγουρέψου ότι δεν μπλοκάρει το default behavior
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })*/


        // Χειρισμός clicks στο μενού
        navView.setNavigationItemSelectedListener {
            //Log.d("DrawerClick", "Clicked: ${menuItem.title}") // ✅ Έλεγχος αν καταγράφεται το κλικ


            when (it.itemId) {
                R.id.menu_settings -> {
                    Log.d("DrawerDebug", "Settings clicked")
                    // Μεταφορά σε Settings Activity
                    //startActivity(Intent(this, SettingsActivity::class.java))
                    Toast.makeText(this, "Yamete kudasai", Toast.LENGTH_SHORT).show()


                }
                R.id.menu_logout -> {
                    // Λειτουργία Logout
                    Log.d("DrawerDebug", "Logout clicked")
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

    private fun openCamera() {
        try {
            val photoFile = createImageFile()
            photoUri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", photoFile)

            // Instead of startActivityForResult, we use takePictureLauncher
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
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




