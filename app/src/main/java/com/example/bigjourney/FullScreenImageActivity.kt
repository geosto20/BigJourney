package com.example.bigjourney

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage

class FullScreenImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_image)

        val imageView: ImageView = findViewById(R.id.fullscreenImageView)
        val deleteButton: Button = findViewById(R.id.deleteButton)

        val imageUrl = intent.getStringExtra("imageUrl")
        val imagePath = intent.getStringExtra("imagePath") // Χρειάζεται για το delete

        if (imageUrl != null) {
            Glide.with(this).load(imageUrl).into(imageView)
        }

        deleteButton.setOnClickListener {
            imagePath?.let { path ->
                deleteImageFromFirebase(path)
            }
        }
    }

    private fun deleteImageFromFirebase(imagePath: String) {
        val storageRef = FirebaseStorage.getInstance().reference.child(imagePath)

        storageRef.delete().addOnSuccessListener {
            Toast.makeText(this, "Image deleted successfully", Toast.LENGTH_SHORT).show()
            finish()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to delete image", Toast.LENGTH_SHORT).show()
        }
    }
}

