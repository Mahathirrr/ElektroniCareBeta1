package com.example.elektronicarebeta1

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.elektronicarebeta1.firebase.FirebaseManager
import com.example.elektronicarebeta1.models.Service
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class BookingActivity : AppCompatActivity() {
    
    private lateinit var serviceNameText: TextView
    private lateinit var servicePriceText: TextView
    private lateinit var deviceTypeSpinner: Spinner
    private lateinit var deviceModelEdit: EditText
    private lateinit var issueDescriptionEdit: EditText
    private lateinit var scheduleDateButton: Button
    private lateinit var deviceImageView: ImageView
    private lateinit var uploadImageButton: LinearLayout
    private lateinit var submitButton: Button
    
    private var selectedDate: Date? = null
    private var selectedImageUri: Uri? = null
    private var serviceId: String? = null
    private var serviceName: String? = null
    private var servicePrice: Double? = null
    
    // Activity result launcher for image selection
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
                deviceImageView.setImageURI(uri)
                deviceImageView.visibility = View.VISIBLE
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)
        
        // Get service details from intent
        serviceId = intent.getStringExtra("SERVICE_ID")
        serviceName = intent.getStringExtra("SERVICE_NAME")
        servicePrice = intent.getDoubleExtra("SERVICE_PRICE", 0.0)
        
        if (serviceId == null || serviceName == null) {
            Toast.makeText(this, "Invalid service information", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        // Initialize views
        serviceNameText = findViewById(R.id.service_name)
        servicePriceText = findViewById(R.id.service_price)
        deviceTypeSpinner = findViewById(R.id.device_type_spinner)
        deviceModelEdit = findViewById(R.id.device_model_edit)
        issueDescriptionEdit = findViewById(R.id.issue_description_edit)
        scheduleDateButton = findViewById(R.id.schedule_date_button)
        deviceImageView = findViewById(R.id.device_image)
        uploadImageButton = findViewById(R.id.upload_image_area)
        submitButton = findViewById(R.id.submit_button)
        
        val backButton = findViewById<ImageView>(R.id.back_button)
        
        // Set service details
        serviceNameText.text = serviceName
        servicePriceText.text = "Rp ${servicePrice?.toInt()}"
        
        // Set up device type spinner
        val deviceTypes = arrayOf("Phone", "Laptop", "TV", "Printer", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, deviceTypes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        deviceTypeSpinner.adapter = adapter
        
        // Set up date picker
        scheduleDateButton.setOnClickListener {
            showDatePicker()
        }
        
        // Set up image upload
        uploadImageButton.setOnClickListener {
            openImagePicker()
        }
        
        // Set up back button
        backButton.setOnClickListener {
            finish()
        }
        
        // Set up submit button
        submitButton.setOnClickListener {
            submitBooking()
        }
    }
    
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                
                selectedDate = calendar.time
                updateDateButtonText()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        
        // Set minimum date to tomorrow
        val tomorrow = Calendar.getInstance()
        tomorrow.add(Calendar.DAY_OF_MONTH, 1)
        datePickerDialog.datePicker.minDate = tomorrow.timeInMillis
        
        datePickerDialog.show()
    }
    
    private fun updateDateButtonText() {
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        selectedDate?.let {
            scheduleDateButton.text = dateFormat.format(it)
        }
    }
    
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }
    
    private fun submitBooking() {
        // Validate inputs
        val deviceType = deviceTypeSpinner.selectedItem.toString()
        val deviceModel = deviceModelEdit.text.toString().trim()
        val issueDescription = issueDescriptionEdit.text.toString().trim()
        
        if (deviceModel.isEmpty()) {
            Toast.makeText(this, "Please enter device model", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (issueDescription.isEmpty()) {
            Toast.makeText(this, "Please describe the issue", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (selectedDate == null) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Disable submit button to prevent multiple submissions
        submitButton.isEnabled = false
        
        lifecycleScope.launch {
            // First upload image if selected
            var imageUrl: String? = null
            if (selectedImageUri != null) {
                imageUrl = FirebaseManager.uploadRepairImage(selectedImageUri!!)
                if (imageUrl == null) {
                    runOnUiThread {
                        Toast.makeText(this@BookingActivity, "Failed to upload image", Toast.LENGTH_SHORT).show()
                        submitButton.isEnabled = true
                    }
                    return@launch
                }
            }
            
            // Create repair request data
            val repairData: HashMap<String, Any?> = hashMapOf(
                "deviceType" to deviceType,
                "deviceModel" to deviceModel,
                "issueDescription" to issueDescription,
                "serviceId" to serviceId,
                "status" to "pending_confirmation", // Changed status
                "estimatedCost" to servicePrice,
                "scheduledDate" to selectedDate,
                "location" to "ElektroniCare Service Center",
                "technicianEmail" to "agusseptiawanasep@gmail.com" // Added technicianEmail
            )
            
            // Add image URL if available
            if (imageUrl != null) {
                repairData["deviceImageUrl"] = imageUrl
            }
            
            // Submit repair request
            val repairId = FirebaseManager.createRepairRequest(repairData as Map<String, Any?>)
            
            if (repairId != null) {
                runOnUiThread {
                    // Create an AlertDialog.Builder
                    val builder = AlertDialog.Builder(this@BookingActivity)
                    
                    // Inflate dialog_submission_success.xml for the dialog's view
                    val dialogView = layoutInflater.inflate(R.layout.dialog_submission_success, null)
                    builder.setView(dialogView)
                    
                    // Get references to views from the inflated layout
                    val repairIdText = dialogView.findViewById<TextView>(R.id.repair_id_text)
                    val whatsappButton = dialogView.findViewById<Button>(R.id.whatsapp_button)
                    val doneButton = dialogView.findViewById<Button>(R.id.done_button)
                    
                    // Set the text of repair_id_text
                    repairIdText.text = "Your Repair ID: $repairId"
                    
                    // Create the AlertDialog instance
                    val dialog = builder.create()
                    
                    // Set up whatsapp_button's OnClickListener
                    whatsappButton.setOnClickListener {
                        val currentRepairId = repairId // Capture for use in this listener
                        val currentDeviceType = deviceTypeSpinner.selectedItem.toString()
                        val currentDeviceModel = deviceModelEdit.text.toString().trim()
                        val currentIssueDescription = issueDescriptionEdit.text.toString().trim()

                        val message = "Hello, I've submitted a repair request through ElektroniCare.\n" +
                                      "My Repair ID: $currentRepairId\n" +
                                      "Device: $currentDeviceType - $currentDeviceModel\n" +
                                      "Issue: $currentIssueDescription\n\n" +
                                      "Please provide assistance."
                        
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse("https://api.whatsapp.com/send?text=" + Uri.encode(message))
                        
                        if (intent.resolveActivity(packageManager) != null) {
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@BookingActivity, "WhatsApp is not installed.", Toast.LENGTH_SHORT).show()
                        }
                        
                        dialog.dismiss()
                        val dashboardIntent = Intent(this@BookingActivity, DashboardActivity::class.java)
                        dashboardIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(dashboardIntent)
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        finish()
                    }
                    
                    // Set up done_button's OnClickListener
                    doneButton.setOnClickListener {
                        dialog.dismiss()
                        val intent = Intent(this@BookingActivity, DashboardActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        finish()
                    }
                    
                    // Make the dialog not cancelable by back press
                    dialog.setCancelable(false)
                    
                    // Show the dialog
                    dialog.show()
                }
            } else {
                runOnUiThread {
                    Toast.makeText(this@BookingActivity, "Failed to submit booking", Toast.LENGTH_SHORT).show()
                    submitButton.isEnabled = true
                }
            }
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}