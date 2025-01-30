package com.example.bigjourney

import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bigjourney.adapters.ImageAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import java.io.File

class ImagesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var imageList: MutableList<Uri>
    private lateinit var deleteButton: Button // Δήλωση του κουμπιού διαγραφής

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_images)

        recyclerView = findViewById(R.id.recyclerView)
        deleteButton = findViewById(R.id.deleteButton)

        // Αρχικά κρύβουμε το κουμπί διαγραφής
        deleteButton.visibility = View.GONE

        recyclerView.layoutManager = GridLayoutManager(this, 3)
        imageList = getAllImages().toMutableList()

        imageAdapter = ImageAdapter(
            this, // Αντί για itemView.context
            imageList,
            onItemClick = { imageUri ->
                // Άνοιγμα εικόνας σε πλήρη οθόνη
                val intent = Intent(this, FullScreenImageActivity::class.java)
                intent.putExtra("imageUri", imageUri.toString())
                startActivity(intent)
            },
            onItemLongClick = {
                updateActionMode() // Ενημέρωση για τη λειτουργία πολλαπλής επιλογής
            }
        )
        recyclerView.adapter = imageAdapter

        setupDeleteButton()
    }

    private fun getAllImages(): List<Uri> {
        val imageUris = mutableListOf<Uri>()
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        storageDir?.listFiles()?.forEach {
            if (it.extension == "jpg") {
                val uri = Uri.fromFile(it)
                imageUris.add(uri)
            }
        }
        return imageUris
    }

    private fun setupDeleteButton() {
        deleteButton.setOnClickListener {
            val selectedItems = imageAdapter.getSelectedItems()
            if (selectedItems.isNotEmpty()) {
                deleteImages(selectedItems)
            } else {
                Toast.makeText(this, "No images selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteImages(selectedImages: List<Uri>) {
        selectedImages.forEach { uri ->
            val file = File(uri.path!!)
            if (file.exists()) {
                file.delete()
            }
        }

        // Διαγραφή από τη λίστα και ενημέρωση του RecyclerView
        imageAdapter.deleteSelectedItems()
        Toast.makeText(this, "Selected images deleted", Toast.LENGTH_SHORT).show()

        // Κρύβουμε το κουμπί διαγραφής αφού διαγράψουμε
        deleteButton.visibility = View.GONE
    }

    fun updateActionMode() {
        val selectedCount = imageAdapter.getSelectedItems().size
        if (selectedCount > 0) {
            // Εμφάνιση του κουμπιού διαγραφής
            deleteButton.visibility = View.VISIBLE
            title = "$selectedCount selected"
        } else {
            // Απόκρυψη του κουμπιού διαγραφής
            deleteButton.visibility = View.GONE
            title = "Gallery"
        }
    }
}




