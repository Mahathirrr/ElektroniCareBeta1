package com.example.elektronicarebeta1.models

import com.google.firebase.firestore.DocumentSnapshot
import java.util.Date

data class User(
    val id: String,
    val fullName: String,
    val email: String,
    val phone: String? = null,
    val address: String? = null,
    val profileImageUrl: String? = null,
    val createdAt: Date? = null
) {
    companion object {
        fun fromDocument(document: DocumentSnapshot): User? {
            return try {
                val id = document.id
                val fullName = document.getString("fullName") ?: ""
                val email = document.getString("email") ?: ""
                val phone = document.getString("phone")
                val address = document.getString("address")
                val profileImageUrl = document.getString("profileImageUrl")
                val createdAt = document.getDate("createdAt")
                
                User(
                    id = id,
                    fullName = fullName,
                    email = email,
                    phone = phone,
                    address = address,
                    profileImageUrl = profileImageUrl,
                    createdAt = createdAt
                )
            } catch (e: Exception) {
                null
            }
        }
    }
    
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "fullName" to fullName,
            "email" to email,
            "phone" to phone,
            "address" to address,
            "profileImageUrl" to profileImageUrl,
            "createdAt" to createdAt
        )
    }
}