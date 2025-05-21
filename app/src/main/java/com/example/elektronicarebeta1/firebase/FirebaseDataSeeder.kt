package com.example.elektronicarebeta1.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.Calendar
import kotlin.random.Random

/**
 * Utility class to seed Firebase with mock data
 */
object FirebaseDataSeeder {
    private const val TAG = "FirebaseDataSeeder"
    
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    
    // Collection references
    private const val USERS_COLLECTION = "users"
    private const val REPAIRS_COLLECTION = "repairs"
    private const val TECHNICIANS_COLLECTION = "technicians"
    private const val SERVICES_COLLECTION = "services"
    
    /**
     * Seeds all collections with mock data
     */
    suspend fun seedAllData(context: Context) {
        try {
            // Only seed if collections are empty
            val usersCount = db.collection(USERS_COLLECTION).limit(1).get().await().size()
            val techniciansCount = db.collection(TECHNICIANS_COLLECTION).limit(1).get().await().size()
            val servicesCount = db.collection(SERVICES_COLLECTION).limit(1).get().await().size()
            
            if (usersCount == 0) {
                seedCurrentUser()
            }
            
            if (techniciansCount == 0) {
                seedTechnicians()
            }
            
            if (servicesCount == 0) {
                seedServices()
            }
            
            // Always seed repairs for the current user
            seedRepairsForCurrentUser()
            
            Log.d(TAG, "Firebase data seeding completed successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error seeding Firebase data", e)
        }
    }
    
    /**
     * Seeds the current user's data
     */
    private suspend fun seedCurrentUser() {
        val currentUser = auth.currentUser ?: return
        
        val userData: HashMap<String, Any> = hashMapOf(
            "fullName" to (currentUser.displayName ?: "John Doe"),
            "email" to (currentUser.email ?: ""),
            "phone" to "+62812345678",
            "address" to "Jl. Sudirman No. 123, Jakarta",
            "profileImageUrl" to (currentUser.photoUrl?.toString() ?: ""),
            "createdAt" to Date()
        )
        
        try {
            db.collection(USERS_COLLECTION).document(currentUser.uid).set(userData as Map<String, Any>).await()
            Log.d(TAG, "User data seeded successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error seeding user data", e)
        }
    }
    
    /**
     * Seeds technicians collection with mock data
     */
    private suspend fun seedTechnicians() {
        val technicians = listOf(
            hashMapOf<String, Any>(
                "fullName" to "Ahmad Rizki",
                "specialization" to "Phone Repair Specialist",
                "experience" to 5,
                "rating" to 4.8,
                "totalReviews" to 124,
                "bio" to "Experienced phone repair technician with expertise in iPhone and Samsung devices.",
                "profileImageUrl" to "https://randomuser.me/api/portraits/men/1.jpg",
                "location" to "Jakarta Selatan",
                "contactNumber" to "+6281234567890",
                "email" to "ahmad.rizki@example.com",
                "availableDays" to listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"),
                "createdAt" to Date()
            ),
            hashMapOf<String, Any>(
                "fullName" to "Siti Nurhayati",
                "specialization" to "Laptop Repair Expert",
                "experience" to 7,
                "rating" to 4.9,
                "totalReviews" to 98,
                "bio" to "Certified laptop repair technician with over 7 years of experience in fixing various laptop brands.",
                "profileImageUrl" to "https://randomuser.me/api/portraits/women/2.jpg",
                "location" to "Jakarta Pusat",
                "contactNumber" to "+6281234567891",
                "email" to "siti.nurhayati@example.com",
                "availableDays" to listOf("Monday", "Wednesday", "Friday", "Saturday"),
                "createdAt" to Date()
            ),
            hashMapOf<String, Any>(
                "fullName" to "Budi Santoso",
                "specialization" to "TV & Electronics Repair",
                "experience" to 10,
                "rating" to 4.7,
                "totalReviews" to 156,
                "bio" to "Specialized in repairing TVs, home theaters, and other electronic devices.",
                "profileImageUrl" to "https://randomuser.me/api/portraits/men/3.jpg",
                "location" to "Jakarta Barat",
                "contactNumber" to "+6281234567892",
                "email" to "budi.santoso@example.com",
                "availableDays" to listOf("Tuesday", "Thursday", "Saturday", "Sunday"),
                "createdAt" to Date()
            ),
            hashMapOf<String, Any>(
                "fullName" to "Dewi Lestari",
                "specialization" to "Printer & Scanner Repair",
                "experience" to 4,
                "rating" to 4.6,
                "totalReviews" to 67,
                "bio" to "Expert in printer and scanner repairs for all major brands.",
                "profileImageUrl" to "https://randomuser.me/api/portraits/women/4.jpg",
                "location" to "Jakarta Timur",
                "contactNumber" to "+6281234567893",
                "email" to "dewi.lestari@example.com",
                "availableDays" to listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"),
                "createdAt" to Date()
            ),
            hashMapOf<String, Any>(
                "fullName" to "Eko Prasetyo",
                "specialization" to "All-around Electronics Technician",
                "experience" to 8,
                "rating" to 4.5,
                "totalReviews" to 112,
                "bio" to "Versatile technician capable of fixing various electronic devices.",
                "profileImageUrl" to "https://randomuser.me/api/portraits/men/5.jpg",
                "location" to "Jakarta Utara",
                "contactNumber" to "+6281234567894",
                "email" to "eko.prasetyo@example.com",
                "availableDays" to listOf("Wednesday", "Thursday", "Friday", "Saturday", "Sunday"),
                "createdAt" to Date()
            )
        )
        
        try {
            for (technician in technicians) {
                db.collection(TECHNICIANS_COLLECTION).add(technician as Map<String, Any>).await()
            }
            Log.d(TAG, "Technicians data seeded successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error seeding technicians data", e)
        }
    }
    
    /**
     * Seeds services collection with mock data
     */
    private suspend fun seedServices() {
        val services = listOf(
            hashMapOf<String, Any>(
                "name" to "Screen Replacement",
                "description" to "Replace damaged or cracked screens with high-quality parts.",
                "category" to "Phone",
                "basePrice" to 750000.0,
                "estimatedTime" to "1-2 hours",
                "imageUrl" to "https://example.com/images/screen_replacement.jpg",
                "createdAt" to Date()
            ),
            hashMapOf<String, Any>(
                "name" to "Battery Replacement",
                "description" to "Replace old or damaged batteries with new ones for better performance.",
                "category" to "Phone",
                "basePrice" to 350000.0,
                "estimatedTime" to "30-60 minutes",
                "imageUrl" to "https://example.com/images/battery_replacement.jpg",
                "createdAt" to Date()
            ),
            hashMapOf<String, Any>(
                "name" to "Water Damage Repair",
                "description" to "Fix water-damaged devices with thorough cleaning and component replacement.",
                "category" to "Phone",
                "basePrice" to 850000.0,
                "estimatedTime" to "1-3 days",
                "imageUrl" to "https://example.com/images/water_damage.jpg",
                "createdAt" to Date()
            ),
            hashMapOf<String, Any>(
                "name" to "Laptop Screen Replacement",
                "description" to "Replace damaged laptop screens with compatible high-quality displays.",
                "category" to "Laptop",
                "basePrice" to 1200000.0,
                "estimatedTime" to "1-2 hours",
                "imageUrl" to "https://example.com/images/laptop_screen.jpg",
                "createdAt" to Date()
            ),
            hashMapOf<String, Any>(
                "name" to "Laptop Keyboard Replacement",
                "description" to "Replace damaged or non-functioning laptop keyboards.",
                "category" to "Laptop",
                "basePrice" to 550000.0,
                "estimatedTime" to "1 hour",
                "imageUrl" to "https://example.com/images/laptop_keyboard.jpg",
                "createdAt" to Date()
            ),
            hashMapOf<String, Any>(
                "name" to "TV Panel Repair",
                "description" to "Fix or replace damaged TV panels for better viewing experience.",
                "category" to "TV",
                "basePrice" to 1500000.0,
                "estimatedTime" to "1-2 days",
                "imageUrl" to "https://example.com/images/tv_panel.jpg",
                "createdAt" to Date()
            ),
            hashMapOf<String, Any>(
                "name" to "Printer Maintenance",
                "description" to "Clean and maintain printers for optimal performance.",
                "category" to "Printer",
                "basePrice" to 250000.0,
                "estimatedTime" to "1 hour",
                "imageUrl" to "https://example.com/images/printer_maintenance.jpg",
                "createdAt" to Date()
            ),
            hashMapOf<String, Any>(
                "name" to "Data Recovery",
                "description" to "Recover lost data from damaged storage devices.",
                "category" to "Laptop",
                "basePrice" to 800000.0,
                "estimatedTime" to "1-3 days",
                "imageUrl" to "https://example.com/images/data_recovery.jpg",
                "createdAt" to Date()
            )
        )
        
        try {
            for (service in services) {
                db.collection(SERVICES_COLLECTION).add(service as Map<String, Any>).await()
            }
            Log.d(TAG, "Services data seeded successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error seeding services data", e)
        }
    }
    
    /**
     * Seeds repairs collection with mock data for the current user
     */
    private suspend fun seedRepairsForCurrentUser() {
        val currentUser = auth.currentUser ?: return
        
        // Get technicians and services for reference
        val technicians = db.collection(TECHNICIANS_COLLECTION).get().await().documents
        val services = db.collection(SERVICES_COLLECTION).get().await().documents
        
        if (technicians.isEmpty() || services.isEmpty()) {
            Log.e(TAG, "Cannot seed repairs: technicians or services collection is empty")
            return
        }
        
        // Check if user already has repairs
        val existingRepairs = db.collection(REPAIRS_COLLECTION)
            .whereEqualTo("userId", currentUser.uid)
            .get()
            .await()
        
        if (existingRepairs.size() >= 2) {
            Log.d(TAG, "User already has repairs, skipping repair seeding")
            return
        }
        
        // Create mock repairs
        val calendar = Calendar.getInstance()
        
        val repairs = listOf(
            hashMapOf<String, Any?>( // Allow nulls for completedDate initially
                "userId" to currentUser.uid,
                "deviceType" to "Phone",
                "deviceModel" to "iPhone 13",
                "issueDescription" to "Cracked screen needs replacement",
                "serviceId" to services.find { it.getString("name") == "Screen Replacement" }?.id,
                "technicianId" to technicians.find { it.getString("specialization")?.contains("Phone") == true }?.id,
                "status" to "completed",
                "estimatedCost" to 750000.0,
                "scheduledDate" to calendar.apply { add(Calendar.DAY_OF_MONTH, -10) }.time,
                "completedDate" to calendar.apply { add(Calendar.DAY_OF_MONTH, -9) }.time,
                "location" to "Fresh Teknik Service Center",
                "createdAt" to calendar.apply { add(Calendar.DAY_OF_MONTH, -12) }.time
            ),
            hashMapOf<String, Any?>( // Allow nulls for completedDate initially
                "userId" to currentUser.uid,
                "deviceType" to "Laptop",
                "deviceModel" to "MacBook Pro 2022",
                "issueDescription" to "Battery drains quickly and needs replacement",
                "serviceId" to services.find { it.getString("name") == "Battery Replacement" }?.id,
                "technicianId" to technicians.find { it.getString("specialization")?.contains("Laptop") == true }?.id,
                "status" to "in_progress",
                "estimatedCost" to 950000.0,
                "scheduledDate" to calendar.apply { 
                    // Reset to current date
                    time = Date()
                    // Set to tomorrow
                    add(Calendar.DAY_OF_MONTH, 1) 
                }.time,
                "completedDate" to null,
                "location" to "ElektroniCare Service Center",
                "createdAt" to calendar.apply { add(Calendar.DAY_OF_MONTH, -2) }.time
            )
        )
        
        try {
            for (repair in repairs) {
                db.collection(REPAIRS_COLLECTION).add(repair as Map<String, Any?>).await()
            }
            Log.d(TAG, "Repairs data seeded successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error seeding repairs data", e)
        }
    }
}