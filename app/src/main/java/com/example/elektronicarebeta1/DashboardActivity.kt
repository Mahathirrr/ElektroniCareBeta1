package com.example.elektronicarebeta1

import android.content.Intent
import android.os.Bundle
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
        setContentView(R.layout.activity_dashboard)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val currentUser = auth.currentUser
        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        userNameText = findViewById<TextView>(R.id.welcome_text)
        val notificationIcon = findViewById<ImageView>(R.id.notification_icon)

        loadUserData()
        setupBottomNavigation()
        setupRepairCards()

        notificationIcon.setOnClickListener {
            Toast.makeText(this, "Notifications coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadUserData() {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val fullName = document.getString("fullName") ?: "User"
                        val firstName = fullName.split(" ").firstOrNull() ?: fullName
                        userNameText.text = "Welcome back, $firstName!"
                    } else {
                        userNameText.text = "Welcome back!"
                    }
                }
                .addOnFailureListener {
                    userNameText.text = "Welcome back!"
                }
        }
    }

    private fun setupBottomNavigation() {
        val homeNav = findViewById<View>(R.id.nav_home)
        val historyNav = findViewById<View>(R.id.nav_history)
        val servicesNav = findViewById<View>(R.id.nav_services)
        val profileNav = findViewById<View>(R.id.nav_profile)

        historyNav.setOnClickListener {
            Toast.makeText(this, "History coming soon", Toast.LENGTH_SHORT).show()
        }

        servicesNav.setOnClickListener {
            Toast.makeText(this, "Services coming soon", Toast.LENGTH_SHORT).show()
        }

        profileNav.setOnClickListener {
            Toast.makeText(this, "Profile coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRepairCards() {
        val viewAllRecent = findViewById<View>(R.id.view_all_recent)
        val repairCard1 = findViewById<View>(R.id.repair_card_1)
        val repairCard2 = findViewById<View>(R.id.repair_card_2)

        viewAllRecent.setOnClickListener {
            Toast.makeText(this, "View all repairs coming soon", Toast.LENGTH_SHORT).show()
        }

        repairCard1.setOnClickListener {
            Toast.makeText(this, "Repair details coming soon", Toast.LENGTH_SHORT).show()
        }

        repairCard2.setOnClickListener {
            Toast.makeText(this, "Repair details coming soon", Toast.LENGTH_SHORT).show()
        }
    }
}
