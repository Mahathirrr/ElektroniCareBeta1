package com.example.elektronicarebeta1

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.elektronicarebeta1.firebase.FirebaseManager
import com.example.elektronicarebeta1.models.User
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {
    
    private lateinit var fullNameText: TextView
    private lateinit var emailText: TextView
    private lateinit var phoneText: TextView
    private lateinit var addressText: TextView
    private lateinit var profileImageView: ImageView
    
    private var selectedImageUri: Uri? = null
    
    // Activity result launcher for image selection
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
                profileImageView.setImageURI(uri)
                uploadProfileImage(uri)
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        
        // Initialize views
        fullNameText = findViewById(R.id.user_name)
        emailText = findViewById(R.id.user_email)
        phoneText = findViewById(R.id.user_phone)
        addressText = findViewById(R.id.user_address)
        profileImageView = findViewById(R.id.profile_image)
        
        val backButton = findViewById<ImageView>(R.id.back_button)
        val editProfileButton = findViewById<Button>(R.id.edit_profile_button)
        val logoutButton = findViewById<Button>(R.id.logout_button)
        val changePhotoButton = findViewById<Button>(R.id.change_photo_button)
        
        // Set up back button
        backButton.setOnClickListener {
            finish()
        }
        
        // Set up edit profile button
        editProfileButton.setOnClickListener {
            Toast.makeText(this, "Edit profile coming soon", Toast.LENGTH_SHORT).show()
        }
        
        // Set up change photo button
        changePhotoButton.setOnClickListener {
            openImagePicker()
        }
        
        // Set up logout button
        logoutButton.setOnClickListener {
            FirebaseManager.signOut()
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
        
        // Set up bottom navigation
        setupBottomNavigation()
        
        // Load user profile
        loadUserProfile()
    }
    
    private fun setupBottomNavigation() {
        val homeNav = findViewById<View>(R.id.nav_home)
        val historyNav = findViewById<View>(R.id.nav_history)
        val servicesNav = findViewById<View>(R.id.nav_services)
        val profileNav = findViewById<View>(R.id.nav_profile)
        
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
                val user = User.fromDocument(userDoc)
                
                user?.let {
                    fullNameText.text = it.fullName
                    emailText.text = it.email
                    phoneText.text = it.phone ?: "Not set"
                    addressText.text = it.address ?: "Not set"
                    
                    // Load profile image if available
                    if (!it.profileImageUrl.isNullOrEmpty()) {
                        Glide.with(this@ProfileActivity)
                            .load(it.profileImageUrl)
                            .placeholder(R.drawable.profile_placeholder)
                            .error(R.drawable.profile_placeholder)
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
    
    private fun uploadProfileImage(imageUri: Uri) {
        lifecycleScope.launch {
            val loadingToast = Toast.makeText(this@ProfileActivity, "Uploading image...", Toast.LENGTH_SHORT)
            loadingToast.show()
            
            val imageUrl = FirebaseManager.uploadProfileImage(imageUri)
            
            loadingToast.cancel()
            
            if (imageUrl != null) {
                Toast.makeText(this@ProfileActivity, "Profile image updated", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@ProfileActivity, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}