package com.example.bigjourney

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class ProfileActivity : AppCompatActivity() {
    private lateinit var ivProfileImage: ImageView
    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var etAddress: EditText
    private lateinit var etJobTitle: EditText
    private lateinit var spinnerGender: Spinner
    private lateinit var etLinkedIn: EditText
    private lateinit var etInstagram: EditText
    private lateinit var cbEmailNotifications: CheckBox
    private lateinit var cbPushNotifications: CheckBox
    private lateinit var btnSave: Button

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private var imageUri: Uri? = null
    private var currentProfileImageUrl: String? = null

    private lateinit var currentUserData: Map<String, Any>

    private val getImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = it
            ivProfileImage.setImageURI(imageUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        ivProfileImage = findViewById(R.id.ivProfileImage)
        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
        etAddress = findViewById(R.id.etAddress)
        etJobTitle = findViewById(R.id.etJobTitle)
        spinnerGender = findViewById(R.id.spinnerGender)
        etLinkedIn = findViewById(R.id.etLinkedIn)
        etInstagram = findViewById(R.id.etInstagram)
        cbEmailNotifications = findViewById(R.id.cbEmailNotifications)
        cbPushNotifications = findViewById(R.id.cbPushNotifications)
        btnSave = findViewById(R.id.btnSave)

        loadUserProfile()

        ivProfileImage.setOnClickListener {
            getImage.launch("image/*")
        }

        btnSave.setOnClickListener {
            saveUserProfile()
        }
    }

    private fun loadUserProfile() {
        val user = auth.currentUser ?: return
        val userRef = db.collection("users").document(user.uid)

        etEmail.setText(user.email)

        userRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                currentUserData = document.data ?: emptyMap()
                etFullName.setText(document.getString("fullName"))
                etPhone.setText(document.getString("phone"))
                etAddress.setText(document.getString("address"))
                etJobTitle.setText(document.getString("jobTitle"))
                etLinkedIn.setText(document.getString("linkedIn"))
                etInstagram.setText(document.getString("instagram"))
                cbEmailNotifications.isChecked = document.getBoolean("emailNotifications") ?: false
                cbPushNotifications.isChecked = document.getBoolean("pushNotifications") ?: false

                val gender = document.getString("gender")
                val genderArray = resources.getStringArray(R.array.gender_options)
                val genderIndex = genderArray.indexOf(gender)
                if (genderIndex >= 0) {
                    spinnerGender.setSelection(genderIndex)
                }

                currentProfileImageUrl = document.getString("profileImageUrl")
                currentProfileImageUrl?.let {
                    Glide.with(this).load(it).into(ivProfileImage)
                }
            }
        }
    }

    private fun saveUserProfile() {
        val user = auth.currentUser ?: return
        val userRef = db.collection("users").document(user.uid)

        val updatedData = mutableMapOf<String, Any>()

        compareAndUpdate("fullName", etFullName.text.toString(), updatedData)
        compareAndUpdate("phone", etPhone.text.toString(), updatedData)
        compareAndUpdate("address", etAddress.text.toString(), updatedData)
        compareAndUpdate("jobTitle", etJobTitle.text.toString(), updatedData)
        compareAndUpdate("gender", spinnerGender.selectedItem.toString(), updatedData)
        compareAndUpdate("linkedIn", etLinkedIn.text.toString(), updatedData)
        compareAndUpdate("instagram", etInstagram.text.toString(), updatedData)
        compareAndUpdate("emailNotifications", cbEmailNotifications.isChecked, updatedData)
        compareAndUpdate("pushNotifications", cbPushNotifications.isChecked, updatedData)

        if (imageUri != null) {
            uploadProfileImage(user.uid) { imageUploadSuccess, newImageUrl ->
                if (imageUploadSuccess && newImageUrl != null) {
                    updatedData["profileImageUrl"] = newImageUrl
                }
                updateFirestore(userRef, updatedData)
            }
        } else {
            updateFirestore(userRef, updatedData)
        }
    }

    private fun compareAndUpdate(key: String, newValue: Any, updatedData: MutableMap<String, Any>) {
        val oldValue = currentUserData[key]
        if (newValue != oldValue) {
            updatedData[key] = newValue
        }
    }

    private fun uploadProfileImage(userId: String, callback: (Boolean, String?) -> Unit) {
        val storageRef = storage.reference.child("profileImages/$userId.jpg")
        storageRef.putFile(imageUri!!).addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                callback(true, uri.toString())
            }.addOnFailureListener {
                callback(false, null)
            }
        }.addOnFailureListener {
            callback(false, null)
        }
    }

    private fun updateFirestore(userRef: DocumentReference, updatedData: Map<String, Any>) {
        if (updatedData.isNotEmpty()) {
            userRef.update(updatedData).addOnSuccessListener {
                showToast("Profile updated successfully")
            }.addOnFailureListener {
                showToast("Failed to update profile")
            }
        } else {
            showToast("No changes detected")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }



}



















