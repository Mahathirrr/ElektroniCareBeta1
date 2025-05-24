package com.example.elektronicarebeta1

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.elektronicarebeta1.firebase.FirebaseManager
import com.example.elektronicarebeta1.models.Repair
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryActivity : AppCompatActivity() {
    
    private lateinit var repairsContainer: LinearLayout
    private lateinit var noRepairsView: View
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        
        // Initialize views
        repairsContainer = findViewById(R.id.repairs_container)
        noRepairsView = findViewById(R.id.no_repairs_view)
        
        // Set up back button
        val backButton = findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }
        
        // Set up bottom navigation
        setupBottomNavigation()
        
        // Load repair history
        loadRepairHistory()
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
        
        servicesNav.setOnClickListener {
            startActivity(Intent(this, ServicesActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
        
        profileNav.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }
    
    private fun loadRepairHistory() {
        lifecycleScope.launch {
            val repairsSnapshot = FirebaseManager.getUserRepairs()
            
            if (repairsSnapshot == null || repairsSnapshot.isEmpty) {
                noRepairsView.visibility = View.VISIBLE
                repairsContainer.visibility = View.GONE
                return@launch
            }
            
            noRepairsView.visibility = View.GONE
            repairsContainer.visibility = View.VISIBLE
            repairsContainer.removeAllViews()
            
            for (document in repairsSnapshot.documents) {
                val repair = Repair.fromDocument(document) ?: continue
                addRepairToView(repair)
            }
        }
    }
    
    private fun addRepairToView(repair: Repair) {
        val repairView = layoutInflater.inflate(R.layout.item_repair_history, repairsContainer, false)
        
        // Set repair details
        val deviceNameText = repairView.findViewById<TextView>(R.id.device_name)
        val serviceTypeText = repairView.findViewById<TextView>(R.id.service_type)
        val dateText = repairView.findViewById<TextView>(R.id.repair_date)
        val locationText = repairView.findViewById<TextView>(R.id.repair_location)
        val statusText = repairView.findViewById<TextView>(R.id.repair_status)
        val priceText = repairView.findViewById<TextView>(R.id.repair_price)
        
        deviceNameText.text = repair.deviceModel
        serviceTypeText.text = repair.issueDescription
        
        val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
        val dateString = repair.appointmentTimestamp?.let { dateFormat.format(it) } ?: "Not scheduled"
        dateText.text = dateString
        
        locationText.text = repair.location ?: "Not specified"
        
        // Set status with appropriate color
        statusText.text = when (repair.status) {
            "completed" -> "Completed"
            "in_progress" -> "In Progress"
            "cancelled" -> "Cancelled"
            else -> "Pending"
        }
        
        statusText.setBackgroundResource(
            when (repair.status) {
                "completed" -> R.drawable.status_completed_bg
                "in_progress" -> R.drawable.status_inprogress_bg
                "cancelled" -> R.drawable.status_cancelled_bg
                else -> R.drawable.status_inprogress_bg
            }
        )
        
        // Set price
        val priceString = repair.estimatedCost?.let { "Rp${String.format("%,.0f", it)}" } ?: "TBD"
        priceText.text = priceString
        
        // Set click listener
        repairView.setOnClickListener {
            Toast.makeText(this, "Repair details coming soon", Toast.LENGTH_SHORT).show()
            // TODO: Navigate to repair details
            // val intent = Intent(this, RepairDetailsActivity::class.java)
            // intent.putExtra("REPAIR_ID", repair.id)
            // startActivity(intent)
        }
        
        repairsContainer.addView(repairView)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}