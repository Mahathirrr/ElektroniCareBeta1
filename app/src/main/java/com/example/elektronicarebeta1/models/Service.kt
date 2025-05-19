package com.example.elektronicarebeta1.models

import com.google.firebase.firestore.DocumentSnapshot
import java.util.Date

data class Service(
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val basePrice: Double,
    val estimatedTime: String? = null,
    val imageUrl: String? = null,
    val createdAt: Date? = null
) {
    companion object {
        fun fromDocument(document: DocumentSnapshot): Service? {
            return try {
                val id = document.id
                val name = document.getString("name") ?: ""
                val description = document.getString("description") ?: ""
                val category = document.getString("category") ?: ""
                val basePrice = document.getDouble("basePrice") ?: 0.0
                val estimatedTime = document.getString("estimatedTime")
                val imageUrl = document.getString("imageUrl")
                val createdAt = document.getDate("createdAt")
                
                Service(
                    id = id,
                    name = name,
                    description = description,
                    category = category,
                    basePrice = basePrice,
                    estimatedTime = estimatedTime,
                    imageUrl = imageUrl,
                    createdAt = createdAt
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}