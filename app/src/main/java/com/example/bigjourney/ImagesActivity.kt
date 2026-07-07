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
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class ImagesActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ImageAdapter
    private lateinit var deleteButton: Button
    private val imageList = mutableListOf<Pair<String, String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_images)

        recyclerView = findViewById(R.id.recyclerView)
        deleteButton = findViewById(R.id.deleteButton)
        deleteButton.visibility = View.GONE

        recyclerView.layoutManager = GridLayoutManager(this, 2)
        adapter = ImageAdapter(imageList) { isSelectionMode ->
            deleteButton.visibility = if (isSelectionMode) View.VISIBLE else View.GONE
        }
        recyclerView.adapter = adapter

        deleteButton.setOnClickListener {
            deleteSelectedImages()
        }

        loadImagesFromFirebase()
    }

    private fun loadImagesFromFirebase() {
        val storageRef = FirebaseStorage.getInstance().reference.child("images/")

        storageRef.listAll().addOnSuccessListener { listResult ->
            imageList.clear()
            for (fileRef in listResult.items) {
                fileRef.downloadUrl.addOnSuccessListener { uri ->
                    val imagePath = fileRef.path
                    imageList.add(Pair(uri.toString(), imagePath))
                    adapter.notifyDataSetChanged()
                }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load images", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteSelectedImages() {
        val selectedImages = adapter.getSelectedImages()
        if (selectedImages.isEmpty()) return

        val storageRef = FirebaseStorage.getInstance().reference

        selectedImages.forEach { imagePath ->
            storageRef.child(imagePath).delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to delete: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        imageList.removeAll { selectedImages.contains(it.second) }
        adapter.clearSelection()
        deleteButton.visibility = View.GONE
    }
}







