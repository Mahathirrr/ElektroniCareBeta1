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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.elektronicarebeta1.firebase.FirebaseManager
import com.example.elektronicarebeta1.models.Service
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class ServicesActivity : AppCompatActivity(), ServiceAdapter.OnItemClickListener {
    
    private lateinit var servicesRecyclerView: RecyclerView
    private lateinit var serviceAdapter: ServiceAdapter
    private lateinit var noServicesView: View
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_services)
        
        // Initialize views
        servicesRecyclerView = findViewById(R.id.services_recycler_view)
        noServicesView = findViewById(R.id.no_services_view)
        
        // Set up RecyclerView
        servicesRecyclerView.layoutManager = LinearLayoutManager(this)
        serviceAdapter = ServiceAdapter(emptyList())
        serviceAdapter.setOnItemClickListener(this)
        servicesRecyclerView.adapter = serviceAdapter
        
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
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
        
        historyNav.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
        
        profileNav.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }
    
    private fun loadServices() {
        lifecycleScope.launch {
            val servicesSnapshot = FirebaseManager.getAllServices()
            
            if (servicesSnapshot == null || servicesSnapshot.isEmpty) {
                noServicesView.visibility = View.VISIBLE
                servicesRecyclerView.visibility = View.GONE
            } else {
                noServicesView.visibility = View.GONE
                servicesRecyclerView.visibility = View.VISIBLE
                
                val servicesList = servicesSnapshot.documents.mapNotNull { document ->
                    Service.fromDocument(document)
                }
                serviceAdapter.updateServices(servicesList)
            }
        }
    }

    override fun onItemClick(service: Service) {
        val intent = Intent(this, BookingActivity::class.java).apply {
            putExtra("SERVICE_ID", service.id)
            putExtra("SERVICE_NAME", service.name)
            putExtra("SERVICE_PRICE", service.basePrice)
        }
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}