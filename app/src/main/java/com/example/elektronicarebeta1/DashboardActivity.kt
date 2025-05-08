package com.example.elektronicarebeta1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DashboardActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var userNameText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            Log.d("DashboardActivity", "Setting content view")
            setContentView(R.layout.activity_dashboard)
            
            // Initialize Firebase instances
            Log.d("DashboardActivity", "Initializing Firebase")
            auth = FirebaseAuth.getInstance()
            db = FirebaseFirestore.getInstance()
            
            // Check if user is authenticated
            val currentUser = auth.currentUser
            if (currentUser == null) {
                Log.e("DashboardActivity", "No authenticated user found")
                Toast.makeText(this, "Authentication error. Please login again.", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                return
            }
            
            Log.d("DashboardActivity", "User authenticated: ${currentUser.email}")
            
            // Initialize views
            Log.d("DashboardActivity", "Initializing views")
            userNameText = findViewById<TextView>(R.id.welcome_text)
            val notificationIcon = findViewById<ImageView>(R.id.notification_icon)

            Toast.makeText(this, "Welcome to Dashboard", Toast.LENGTH_SHORT).show()
            
            // Load user data
            loadUserData()
            
            // Set click listeners for bottom navigation
            setupBottomNavigation()
            
            // Set click listeners for repair cards
            setupRepairCards()
            
            // Handle notification and profile clicks
            notificationIcon.setOnClickListener {
                Toast.makeText(this, "Notifications coming soon", Toast.LENGTH_SHORT).show()
                //            startActivity(Intent(this, NotificationActivity::class.java))
            }

            Log.d("DashboardActivity", "Dashboard setup completed successfully")
        } catch (e: Exception) {
            Log.e("DashboardActivity", "Error in onCreate", e)
            Toast.makeText(this, "Error setting up dashboard: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun loadUserData() {
        try {
            Log.d("DashboardActivity", "Loading user data")
            val currentUser = auth.currentUser
            currentUser?.let { user ->
                Log.d("DashboardActivity", "Fetching user document for UID: ${user.uid}")
                db.collection("users").document(user.uid).get()
                    .addOnSuccessListener { document ->
                        if (document != null && document.exists()) {
                            val fullName = document.getString("fullName") ?: "User"
                            val firstName = fullName.split(" ").firstOrNull() ?: fullName
                            Log.d("DashboardActivity", "User data loaded: $firstName")
                            userNameText.text = "Welcome back, $firstName!"
                        } else {
                            Log.w("DashboardActivity", "User document doesn't exist")
                            userNameText.text = "Welcome back!"
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("DashboardActivity", "Error loading user data", e)
                        userNameText.text = "Welcome back!"
                    }
            }
        } catch (e: Exception) {
            Log.e("DashboardActivity", "Error in loadUserData", e)
            userNameText.text = "Welcome back!"
        }
    }

    private fun setupBottomNavigation() {
        try {
            Log.d("DashboardActivity", "Setting up bottom navigation")
            val homeNav = findViewById<View>(R.id.nav_home)
            val historyNav = findViewById<View>(R.id.nav_history)
            val servicesNav = findViewById<View>(R.id.nav_services)
            val profileNav = findViewById<View>(R.id.nav_profile)

            historyNav.setOnClickListener {
                Toast.makeText(this, "History coming soon", Toast.LENGTH_SHORT).show()
                //            startActivity(Intent(this, NotificationActivity::class.java))
            }

            servicesNav.setOnClickListener {
                Toast.makeText(this, "Services coming soon", Toast.LENGTH_SHORT).show()
                // TODO: Implement services screen navigation
            }

            profileNav.setOnClickListener {
                Toast.makeText(this, "Profile coming soon", Toast.LENGTH_SHORT).show()
                // TODO: Implement profile screen navigation
            }
            
            Log.d("DashboardActivity", "Bottom navigation setup completed")
        } catch (e: Exception) {
            Log.e("DashboardActivity", "Error setting up bottom navigation", e)
        }
    }

    private fun setupRepairCards() {
        try {
            Log.d("DashboardActivity", "Setting up repair cards")
            val viewAllRecent = findViewById<View>(R.id.view_all_recent)
            val repairCard1 = findViewById<View>(R.id.repair_card_1)
            val repairCard2 = findViewById<View>(R.id.repair_card_2)

            viewAllRecent.setOnClickListener {
                Toast.makeText(this, "View all repairs coming soon", Toast.LENGTH_SHORT).show()
                //            startActivity(Intent(this, NotificationActivity::class.java))
            }

            repairCard1.setOnClickListener {
                Toast.makeText(this, "Repair details coming soon", Toast.LENGTH_SHORT).show()
                // TODO: Implement repair details navigation
            }

            repairCard2.setOnClickListener {
                Toast.makeText(this, "Repair details coming soon", Toast.LENGTH_SHORT).show()
                // TODO: Implement repair details navigation
            }
            
            Log.d("DashboardActivity", "Repair cards setup completed")
        } catch (e: Exception) {
            Log.e("DashboardActivity", "Error setting up repair cards", e)
        }
    }
}
