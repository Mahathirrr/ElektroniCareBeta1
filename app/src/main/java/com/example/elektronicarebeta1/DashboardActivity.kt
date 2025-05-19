package com.example.elektronicarebeta1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.elektronicarebeta1.firebase.FirebaseDataSeeder
import com.example.elektronicarebeta1.firebase.FirebaseManager
import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity() {
    private lateinit var userNameText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val currentUser = FirebaseManager.getCurrentUser()
        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        userNameText = findViewById<TextView>(R.id.welcome_text)
        val notificationIcon = findViewById<ImageView>(R.id.notification_icon)

        // Seed Firebase with mock data
        lifecycleScope.launch {
            FirebaseDataSeeder.seedAllData(this@DashboardActivity)
        }

        loadUserData()
        setupBottomNavigation()
        setupRepairCards()

        notificationIcon.setOnClickListener {
            Toast.makeText(this, "Notifications coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadUserData() {
        lifecycleScope.launch {
            val userDoc = FirebaseManager.getUserData()
            
            if (userDoc != null && userDoc.exists()) {
                val fullName = userDoc.getString("fullName") ?: "User"
                val firstName = fullName.split(" ").firstOrNull() ?: fullName
                userNameText.text = "Welcome back, $firstName!"
            } else {
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
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        servicesNav.setOnClickListener {
            startActivity(Intent(this, ServicesActivity::class.java))
        }

        profileNav.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun setupRepairCards() {
        val viewAllRecent = findViewById<View>(R.id.view_all_recent)
        val repairCard1 = findViewById<View>(R.id.repair_card_1)
        val repairCard2 = findViewById<View>(R.id.repair_card_2)

        viewAllRecent.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        repairCard1.setOnClickListener {
            Toast.makeText(this, "Repair details coming soon", Toast.LENGTH_SHORT).show()
        }

        repairCard2.setOnClickListener {
            Toast.makeText(this, "Repair details coming soon", Toast.LENGTH_SHORT).show()
        }
    }
}
