package com.example.elektronicarebeta1.models

import com.google.firebase.firestore.DocumentSnapshot
import java.util.Date

data class Technician(
    val id: String,
    val fullName: String,
    val specialization: String,
    val experience: Int,
    val rating: Double,
    val totalReviews: Int,
    val bio: String? = null,
    val profileImageUrl: String? = null,
    val location: String? = null,
    val contactNumber: String? = null,
    val email: String? = null,
    val availableDays: List<String>? = null,
    val createdAt: Date? = null
) {
    companion object {
        fun fromDocument(document: DocumentSnapshot): Technician? {
            return try {
                val id = document.id
                val fullName = document.getString("fullName") ?: ""
                val specialization = document.getString("specialization") ?: ""
                val experience = document.getLong("experience")?.toInt() ?: 0
                val rating = document.getDouble("rating") ?: 0.0
                val totalReviews = document.getLong("totalReviews")?.toInt() ?: 0
                val bio = document.getString("bio")
                val profileImageUrl = document.getString("profileImageUrl")
                val location = document.getString("location")
                val contactNumber = document.getString("contactNumber")
                val email = document.getString("email")
                val availableDays = document.get("availableDays") as? List<String>
                val createdAt = document.getDate("createdAt")
                
                Technician(
                    id = id,
                    fullName = fullName,
                    specialization = specialization,
                    experience = experience,
                    rating = rating,
                    totalReviews = totalReviews,
                    bio = bio,
                    profileImageUrl = profileImageUrl,
                    location = location,
                    contactNumber = contactNumber,
                    email = email,
                    availableDays = availableDays,
                    createdAt = createdAt
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}