package com.example.elektronicarebeta1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.elektronicarebeta1.firebase.FirebaseManager
import com.example.elektronicarebeta1.models.User
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {
    
    private lateinit var fullNameText: TextView
    private lateinit var emailText: TextView
    private lateinit var phoneText: TextView
    private lateinit var addressText: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        
        // Initialize views
        fullNameText = findViewById(R.id.user_name)
        emailText = findViewById(R.id.user_email)
        phoneText = findViewById(R.id.user_phone)
        addressText = findViewById(R.id.user_address)
        
        val backButton = findViewById<ImageView>(R.id.back_button)
        val editProfileButton = findViewById<Button>(R.id.edit_profile_button)
        val logoutButton = findViewById<Button>(R.id.logout_button)
        
        // Set up back button
        backButton.setOnClickListener {
            finish()
        }
        
        // Set up edit profile button
        editProfileButton.setOnClickListener {
            Toast.makeText(this, "Edit profile coming soon", Toast.LENGTH_SHORT).show()
        }
        
        // Set up logout button
        logoutButton.setOnClickListener {
            FirebaseManager.signOut()
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
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
            finish()
        }
        
        historyNav.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
            finish()
        }
        
        servicesNav.setOnClickListener {
            startActivity(Intent(this, ServicesActivity::class.java))
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
                }
            } else {
                Toast.makeText(this@ProfileActivity, "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
        }
    }
}