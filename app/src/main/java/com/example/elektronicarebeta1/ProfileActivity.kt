package com.example.elektronicarebeta1

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout
import com.example.elektronicarebeta1.firebase.FirebaseManager
import com.example.elektronicarebeta1.models.User
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class ProfileActivity : AppCompatActivity() {
    
    private lateinit var emailText: TextView
    private lateinit var editTextUserName: com.google.android.material.textfield.TextInputEditText
    private lateinit var editTextPhone: com.google.android.material.textfield.TextInputEditText
    private lateinit var editTextAddress: com.google.android.material.textfield.TextInputEditText
    private lateinit var saveProfileButton: Button
    private lateinit var profileImageView: ImageView
    private lateinit var editPhotoIcon: ImageView
    private lateinit var userDateJoinedText: TextView 
    private lateinit var profileSaveProgressBar: ProgressBar 
    
    private var selectedImageUri: Uri? = null
    private var originalUser: User? = null 
    
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
                Glide.with(this@ProfileActivity)
                    .load(uri)
                    .placeholder(R.drawable.profile_placeholder)
                    .error(R.drawable.profile_placeholder)
                    .circleCrop()
                    .into(profileImageView)
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        
        editTextUserName = findViewById(R.id.edit_text_user_name)
        emailText = findViewById(R.id.user_email)
        editTextPhone = findViewById(R.id.edit_text_phone)
        editTextAddress = findViewById(R.id.edit_text_address)
        profileImageView = findViewById(R.id.profile_image)
        editPhotoIcon = findViewById(R.id.edit_photo_icon)
        saveProfileButton = findViewById(R.id.save_profile_button)
        userDateJoinedText = findViewById(R.id.user_date_joined) 
        profileSaveProgressBar = findViewById(R.id.profile_save_progress_bar)
        
        val backButton = findViewById<ImageView>(R.id.back_button)
        val logoutButton = findViewById<Button>(R.id.logout_button)
        
        backButton.setOnClickListener {
            finish()
        }
        
        editPhotoIcon.setOnClickListener {
            openImagePicker()
        }
        
        saveProfileButton.setOnClickListener {
            handleSaveChanges()
        }
        
        logoutButton.setOnClickListener {
            showSignOutConfirmationDialog()
        }
        
        setupBottomNavigation()
        loadUserProfile()

        // Add TextChangedListeners for immediate error clearing
        val textInputLayoutUserName = findViewById<TextInputLayout>(R.id.text_input_layout_user_name)
        editTextUserName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                textInputLayoutUserName.error = null
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        val textInputLayoutPhone = findViewById<TextInputLayout>(R.id.text_input_layout_phone)
        editTextPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                textInputLayoutPhone.error = null
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }
    
    private fun setupBottomNavigation() {
        val homeNav = findViewById<View>(R.id.nav_home)
        val historyNav = findViewById<View>(R.id.nav_history)
        val servicesNav = findViewById<View>(R.id.nav_services)
        
        homeNav.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
        
        historyNav.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
        
        servicesNav.setOnClickListener {
            startActivity(Intent(this, ServicesActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }
    
    private fun loadUserProfile() {
        lifecycleScope.launch {
            val userDoc = FirebaseManager.getUserData()
            
            if (userDoc != null && userDoc.exists()) {
                originalUser = User.fromDocument(userDoc) 
                
                originalUser?.let {
                    editTextUserName.setText(it.fullName)
                    emailText.text = it.email 
                    editTextPhone.setText(it.phone ?: "")
                    editTextAddress.setText(it.address ?: "")
                    
                    it.createdAt?.let { date ->
                        val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
                        userDateJoinedText.text = dateFormat.format(date)
                    } ?: run {
                        userDateJoinedText.text = "N/A" 
                    }
                    
                    if (!it.profileImageUrl.isNullOrEmpty()) {
                        Glide.with(this@ProfileActivity)
                            .load(it.profileImageUrl)
                            .placeholder(R.drawable.profile_placeholder)
                            .error(R.drawable.profile_placeholder)
                            .circleCrop()
                            .into(profileImageView)
                    } else {
                         Glide.with(this@ProfileActivity)
                            .load(R.drawable.profile_placeholder) 
                            .circleCrop()
                            .into(profileImageView)
                    }
                }
            } else {
                Toast.makeText(this@ProfileActivity, "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }
    
    private fun handleSaveChanges() {
        lifecycleScope.launch {
            saveProfileButton.isEnabled = false
            profileSaveProgressBar.visibility = View.VISIBLE

            val textInputLayoutUserName = findViewById<TextInputLayout>(R.id.text_input_layout_user_name)
            val textInputLayoutPhone = findViewById<TextInputLayout>(R.id.text_input_layout_phone)

            val newFullName = editTextUserName.text.toString().trim()
            if (newFullName.isEmpty()) {
                textInputLayoutUserName.error = "Full name cannot be empty"
                profileSaveProgressBar.visibility = View.GONE
                saveProfileButton.isEnabled = true
                return@launch
            } else {
                textInputLayoutUserName.error = null
            }

            val newPhone = editTextPhone.text.toString().trim()
            // Validate phone only if it's not empty, to allow users to clear it if they wish
            if (newPhone.isNotEmpty() && (!newPhone.matches(Regex("^\\+?[0-9]{10,13}$")))) { // Regex for basic international phone format
                textInputLayoutPhone.error = "Enter a valid phone number (e.g., +6281234567890 or 081234567890)"
                profileSaveProgressBar.visibility = View.GONE
                saveProfileButton.isEnabled = true
                return@launch
            } else {
                textInputLayoutPhone.error = null
            }

            try {
                var imageSuccessfullyUploadedOrNotNeeded = true
                var newImageUrlForUpdate: String? = null

                if (selectedImageUri != null) {
                    val newImageUrl = FirebaseManager.uploadProfileImage(selectedImageUri!!)
                    if (newImageUrl != null) {
                        newImageUrlForUpdate = newImageUrl
                        selectedImageUri = null
                    } else {
                        imageSuccessfullyUploadedOrNotNeeded = false
                        Toast.makeText(this@ProfileActivity, "Profile image upload failed. Please try again.", Toast.LENGTH_LONG).show()
                    }
                }

                val updatedData = mutableMapOf<String, Any>()
                val newAddress = editTextAddress.text.toString().trim()

                val nameChanged = originalUser?.fullName != newFullName
                val phoneChanged = originalUser?.phone != newPhone 
                val addressChanged = originalUser?.address != newAddress

                if (nameChanged) updatedData["fullName"] = newFullName
                if (phoneChanged) updatedData["phone"] = newPhone
                if (addressChanged) updatedData["address"] = newAddress

                var textUpdateSucceeded = false
                if (updatedData.isNotEmpty()) {
                    if (FirebaseManager.updateUserData(updatedData)) {
                        textUpdateSucceeded = true
                    } else {
                        Toast.makeText(this@ProfileActivity, "Failed to update profile details.", Toast.LENGTH_LONG).show()
                    }
                } else if (newImageUrlForUpdate != null || !updatedData.isNotEmpty()) {
                    textUpdateSucceeded = true
                }

                if (imageSuccessfullyUploadedOrNotNeeded && textUpdateSucceeded) {
                    Toast.makeText(this@ProfileActivity, "Profile saved successfully!", Toast.LENGTH_LONG).show()
                    loadUserProfile()
                } else if (!imageSuccessfullyUploadedOrNotNeeded) {
                    // Error for image upload already shown
                } else if (updatedData.isNotEmpty() && !textUpdateSucceeded) {
                    // Error for text update already shown
                }
            } finally {
                profileSaveProgressBar.visibility = View.GONE
                saveProfileButton.isEnabled = true
            }
        }
    }

    private fun showSignOutConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_confirm_signout, null)
        builder.setView(dialogView)

        val dialog = builder.create()

        val cancelButton = dialogView.findViewById<Button>(R.id.button_dialog_cancel)
        val signOutButton = dialogView.findViewById<Button>(R.id.button_dialog_signout)

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        signOutButton.setOnClickListener {
            FirebaseManager.signOut()
            val intent = Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
            dialog.dismiss()
        }
        
        dialog.setCancelable(true)
        dialog.show()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}