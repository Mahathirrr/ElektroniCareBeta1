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
    
//    private lateinit var serviceNameText: TextView // Removed, no direct equivalent
//    private lateinit var servicePriceText: TextView // Removed, no direct equivalent
//    private lateinit var deviceTypeSpinner: Spinner // Removed, no direct equivalent
//    private lateinit var deviceModelEdit: EditText // Removed, no direct equivalent for device model
    private lateinit var issueDescriptionEdit: EditText // Maps to etProblemDescription
    private lateinit var scheduleDateLayout: LinearLayout // Was scheduleDateButton, now the layout for date picking
    private lateinit var tvSelectedDate: TextView // New view to display the selected date text
//    private lateinit var deviceImageView: ImageView // Removed, no direct equivalent
    private lateinit var uploadLayout: LinearLayout // Was uploadImageButton, now the layout for upload
    private lateinit var submitButton: Button // Maps to btnSubmitRequest
    
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
//                deviceImageView.setImageURI(uri) // Removed
//                deviceImageView.visibility = View.VISIBLE // Removed
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_request)
        
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
//        serviceNameText = findViewById(R.id.service_name) // Removed
//        servicePriceText = findViewById(R.id.service_price) // Removed
//        deviceTypeSpinner = findViewById(R.id.device_type_spinner) // Removed
//        deviceModelEdit = findViewById(R.id.device_model_edit) // Removed
        issueDescriptionEdit = findViewById(R.id.etProblemDescription) // Corrected ID
        scheduleDateLayout = findViewById(R.id.llDateLayout) // Corrected ID
        tvSelectedDate = findViewById(R.id.tvSelectedDate) // Corrected ID
//        deviceImageView = findViewById(R.id.device_image) // Removed
        uploadLayout = findViewById(R.id.llUploadLayout) // Corrected ID
        submitButton = findViewById(R.id.btnSubmitRequest) // Corrected ID
        
        val backButton = findViewById<ImageView>(R.id.ivBackArrow) // Corrected ID
        
        // Set service details
//        serviceNameText.text = serviceName // Removed
//        servicePriceText.text = "Rp ${servicePrice?.toInt()}" // Removed
        
        // Set up device type spinner
//        val deviceTypes = arrayOf("Phone", "Laptop", "TV", "Printer", "Other") // Removed
//        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, deviceTypes) // Removed
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // Removed
//        deviceTypeSpinner.adapter = adapter // Removed
        
        // Set up date picker
        scheduleDateLayout.setOnClickListener { // Was scheduleDateButton
            showDatePicker()
        }
        
        // Set up image upload
        uploadLayout.setOnClickListener { // Was uploadImageButton
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
            tvSelectedDate.text = dateFormat.format(it) // Was scheduleDateButton, now tvSelectedDate
        }
    }
    
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }
    
    private fun submitBooking() {
        // Validate inputs
//        val deviceType = deviceTypeSpinner.selectedItem.toString() // Removed
//        val deviceModel = deviceModelEdit.text.toString().trim() // Removed
        val issueDescription = issueDescriptionEdit.text.toString().trim()
        
//        if (deviceModel.isEmpty()) { // Removed
//            Toast.makeText(this, "Please enter device model", Toast.LENGTH_SHORT).show() // Removed
//            return // Removed
//        }
        
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
//                "deviceType" to deviceType, // Removed
//                "deviceModel" to deviceModel, // Removed
                "issueDescription" to issueDescription,
                "serviceId" to serviceId, // This remains as it's passed via Intent
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
//                        val currentDeviceType = deviceTypeSpinner.selectedItem.toString() // Removed
//                        val currentDeviceModel = deviceModelEdit.text.toString().trim() // Removed
                        val currentIssueDescription = issueDescriptionEdit.text.toString().trim()

                        // Construct message without deviceType and deviceModel
                        val message = "Hello, I've submitted a repair request through ElektroniCare.\n" +
                                      "My Repair ID: $currentRepairId\n" +
//                                      "Device: $currentDeviceType - $currentDeviceModel\n" + // Removed
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