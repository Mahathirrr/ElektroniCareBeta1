package com.example.elektronicarebeta1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DashboardActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var userNameText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Initialize Firebase instances
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Initialize views
        userNameText = findViewById<TextView>(R.id.welcome_text)
        val notificationIcon = findViewById<ImageView>(R.id.notification_icon)
        val profileIcon = findViewById<ImageView>(R.id.profile_icon)

        // Load user data
        loadUserData()

        // Set click listeners for bottom navigation
        setupBottomNavigation()

        // Set click listeners for repair cards
        setupRepairCards()

        // Handle notification and profile clicks
        notificationIcon.setOnClickListener {
//            startActivity(Intent(this, NotificationActivity::class.java))
        }

        profileIcon.setOnClickListener {
            // TODO: Implement profile screen navigation
        }
    }

    private fun loadUserData() {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val firstName = document.getString("fullName")?.split(" ")?.get(0) ?: "User"
                        userNameText.text = "Welcome back, $firstName!"
                    }
                }
        }
    }

    private fun setupBottomNavigation() {
        val homeNav = findViewById<View>(R.id.nav_home)
        val historyNav = findViewById<View>(R.id.nav_history)
        val servicesNav = findViewById<View>(R.id.nav_services)
        val profileNav = findViewById<View>(R.id.nav_profile)

        historyNav.setOnClickListener {
//            startActivity(Intent(this, NotificationActivity::class.java))
        }

        servicesNav.setOnClickListener {
            // TODO: Implement services screen navigation
        }

        profileNav.setOnClickListener {
            // TODO: Implement profile screen navigation
        }
    }

    private fun setupRepairCards() {
        val viewAllRecent = findViewById<View>(R.id.view_all_recent)
        val repairCard1 = findViewById<View>(R.id.repair_card_1)
        val repairCard2 = findViewById<View>(R.id.repair_card_2)

        viewAllRecent.setOnClickListener {
//            startActivity(Intent(this, NotificationActivity::class.java))
        }

        repairCard1.setOnClickListener {
            // TODO: Implement repair details navigation
        }

        repairCard2.setOnClickListener {
            // TODO: Implement repair details navigation
        }
    }
}
