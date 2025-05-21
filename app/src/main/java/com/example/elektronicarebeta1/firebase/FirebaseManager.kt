package com.example.elektronicarebeta1.firebase

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID

/**
 * Singleton class to manage all Firebase operations
 */
object FirebaseManager {
    private const val TAG = "FirebaseManager"
    
    // Firebase instances
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    
    // Collection references
    private const val USERS_COLLECTION = "users"
    private const val REPAIRS_COLLECTION = "repairs"
    private const val TECHNICIANS_COLLECTION = "technicians"
    private const val SERVICES_COLLECTION = "services"
    
    // Storage references
    private const val PROFILE_IMAGES = "profile_images"
    private const val REPAIR_IMAGES = "repair_images"
    
    // User operations
    fun getCurrentUser(): FirebaseUser? = auth.currentUser
    
    fun getUserId(): String? = auth.currentUser?.uid
    
    suspend fun getUserData(): DocumentSnapshot? {
        val userId = getUserId() ?: return null
        return try {
            db.collection(USERS_COLLECTION).document(userId).get().await()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user data", e)
            null
        }
    }
    
    suspend fun updateUserData(userData: Map<String, Any>): Boolean {
        val userId = getUserId() ?: return false
        return try {
            db.collection(USERS_COLLECTION).document(userId).update(userData).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user data", e)
            false
        }
    }
    
    // Repair operations
    suspend fun getUserRepairs(): QuerySnapshot? {
        val userId = getUserId() ?: return null
        return try {
            db.collection(REPAIRS_COLLECTION)
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user repairs", e)
            null
        }
    }
    
    suspend fun getRepairById(repairId: String): DocumentSnapshot? {
        return try {
            db.collection(REPAIRS_COLLECTION).document(repairId).get().await()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting repair by id", e)
            null
        }
    }
    
    suspend fun createRepairRequest(repairData: Map<String, Any>): String? {
        val userId = getUserId() ?: return null
        val repairWithUser = repairData.toMutableMap()
        repairWithUser.putIfAbsent("createdAt", Date()) // Default if not provided
        repairWithUser.putIfAbsent("status", "pending") // Default status if not in repairData
        repairWithUser["userId"] = userId // Current user's ID always takes precedence
        
        return try {
            val docRef = db.collection(REPAIRS_COLLECTION).add(repairWithUser).await()
            docRef.id
        } catch (e: Exception) {
            Log.e(TAG, "Error creating repair request", e)
            null
        }
    }
    
    // Technician operations
    suspend fun getAllTechnicians(): QuerySnapshot? {
        return try {
            db.collection(TECHNICIANS_COLLECTION).get().await()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting technicians", e)
            null
        }
    }
    
    suspend fun getTechnicianById(technicianId: String): DocumentSnapshot? {
        return try {
            db.collection(TECHNICIANS_COLLECTION).document(technicianId).get().await()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting technician by id", e)
            null
        }
    }
    
    // Service operations
    suspend fun getAllServices(): QuerySnapshot? {
        return try {
            db.collection(SERVICES_COLLECTION).get().await()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting services", e)
            null
        }
    }
    
    suspend fun getServiceById(serviceId: String): DocumentSnapshot? {
        return try {
            db.collection(SERVICES_COLLECTION).document(serviceId).get().await()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting service by id", e)
            null
        }
    }
    
    // Authentication operations
    fun signOut() {
        auth.signOut()
    }
    
    // Storage operations
    suspend fun uploadProfileImage(imageUri: Uri): String? {
        val userId = getUserId() ?: return null
        val filename = "$userId-${UUID.randomUUID()}"
        val storageRef = storage.reference.child("$PROFILE_IMAGES/$filename")
        
        return try {
            // Upload the file
            val uploadTask = storageRef.putFile(imageUri).await()
            
            // Get download URL
            val downloadUrl = storageRef.downloadUrl.await().toString()
            
            // Update user profile with the image URL
            updateUserData(mapOf("profileImageUrl" to downloadUrl))
            
            downloadUrl
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading profile image", e)
            null
        }
    }
    
    suspend fun uploadRepairImage(imageUri: Uri, repairId: String? = null): String? {
        val userId = getUserId() ?: return null
        val filename = "${repairId ?: "new"}-${UUID.randomUUID()}"
        val storageRef = storage.reference.child("$REPAIR_IMAGES/$userId/$filename")
        
        return try {
            // Upload the file
            storageRef.putFile(imageUri).await()
            
            // Get download URL
            storageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading repair image", e)
            null
        }
    }
}