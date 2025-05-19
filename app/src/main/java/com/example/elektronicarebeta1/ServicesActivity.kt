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
import com.example.elektronicarebeta1.models.Service
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class ServicesActivity : AppCompatActivity() {
    
    private lateinit var servicesContainer: LinearLayout
    private lateinit var noServicesView: View
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_services)
        
        // Initialize views
        servicesContainer = findViewById(R.id.services_container)
        noServicesView = findViewById(R.id.no_services_view)
        
        // Set up back button
        val backButton = findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }
        
        // Set up bottom navigation
        setupBottomNavigation()
        
        // Load services
        loadServices()
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
        
        profileNav.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }
    }
    
    private fun loadServices() {
        lifecycleScope.launch {
            val servicesSnapshot = FirebaseManager.getAllServices()
            
            if (servicesSnapshot == null || servicesSnapshot.isEmpty) {
                noServicesView.visibility = View.VISIBLE
                servicesContainer.visibility = View.GONE
                return@launch
            }
            
            noServicesView.visibility = View.GONE
            servicesContainer.visibility = View.VISIBLE
            servicesContainer.removeAllViews()
            
            // Group services by category
            val servicesByCategory = mutableMapOf<String, MutableList<Service>>()
            
            for (document in servicesSnapshot.documents) {
                val service = Service.fromDocument(document) ?: continue
                val category = service.category
                
                if (!servicesByCategory.containsKey(category)) {
                    servicesByCategory[category] = mutableListOf()
                }
                
                servicesByCategory[category]?.add(service)
            }
            
            // Add services to view by category
            for ((category, services) in servicesByCategory) {
                addCategoryHeader(category)
                
                for (service in services) {
                    addServiceToView(service)
                }
                
                // Add spacing between categories
                val spacer = View(this@ServicesActivity)
                spacer.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    resources.getDimensionPixelSize(android.R.dimen.app_icon_size) / 2
                )
                servicesContainer.addView(spacer)
            }
        }
    }
    
    private fun addCategoryHeader(category: String) {
        val headerView = layoutInflater.inflate(R.layout.item_category_header, servicesContainer, false)
        val categoryText = headerView.findViewById<TextView>(R.id.category_name)
        categoryText.text = category
        
        servicesContainer.addView(headerView)
    }
    
    private fun addServiceToView(service: Service) {
        val serviceView = layoutInflater.inflate(R.layout.item_service, servicesContainer, false)
        
        // Set service details
        val serviceNameText = serviceView.findViewById<TextView>(R.id.service_name)
        val serviceDescText = serviceView.findViewById<TextView>(R.id.service_description)
        val servicePriceText = serviceView.findViewById<TextView>(R.id.service_price)
        val serviceTimeText = serviceView.findViewById<TextView>(R.id.service_time)
        
        serviceNameText.text = service.name
        serviceDescText.text = service.description
        
        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        formatter.maximumFractionDigits = 0
        servicePriceText.text = formatter.format(service.basePrice).replace("Rp", "Rp ")
        
        serviceTimeText.text = service.estimatedTime ?: "Varies"
        
        // Set click listener
        serviceView.setOnClickListener {
            Toast.makeText(this, "Service booking coming soon", Toast.LENGTH_SHORT).show()
            // TODO: Navigate to service booking
            // val intent = Intent(this, BookServiceActivity::class.java)
            // intent.putExtra("SERVICE_ID", service.id)
            // startActivity(intent)
        }
        
        servicesContainer.addView(serviceView)
    }
}