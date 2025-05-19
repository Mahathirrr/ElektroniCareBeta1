package com.example.elektronicarebeta1.models

import com.google.firebase.firestore.DocumentSnapshot
import java.util.Date

data class Repair(
    val id: String,
    val userId: String,
    val deviceType: String,
    val deviceModel: String,
    val issueDescription: String,
    val serviceId: String? = null,
    val technicianId: String? = null,
    val status: String,
    val estimatedCost: Double? = null,
    val scheduledDate: Date? = null,
    val completedDate: Date? = null,
    val location: String? = null,
    val createdAt: Date? = null
) {
    companion object {
        fun fromDocument(document: DocumentSnapshot): Repair? {
            return try {
                val id = document.id
                val userId = document.getString("userId") ?: ""
                val deviceType = document.getString("deviceType") ?: ""
                val deviceModel = document.getString("deviceModel") ?: ""
                val issueDescription = document.getString("issueDescription") ?: ""
                val serviceId = document.getString("serviceId")
                val technicianId = document.getString("technicianId")
                val status = document.getString("status") ?: "pending"
                val estimatedCost = document.getDouble("estimatedCost")
                val scheduledDate = document.getDate("scheduledDate")
                val completedDate = document.getDate("completedDate")
                val location = document.getString("location")
                val createdAt = document.getDate("createdAt")
                
                Repair(
                    id = id,
                    userId = userId,
                    deviceType = deviceType,
                    deviceModel = deviceModel,
                    issueDescription = issueDescription,
                    serviceId = serviceId,
                    technicianId = technicianId,
                    status = status,
                    estimatedCost = estimatedCost,
                    scheduledDate = scheduledDate,
                    completedDate = completedDate,
                    location = location,
                    createdAt = createdAt
                )
            } catch (e: Exception) {
                null
            }
        }
    }
    
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "userId" to userId,
            "deviceType" to deviceType,
            "deviceModel" to deviceModel,
            "issueDescription" to issueDescription,
            "serviceId" to serviceId,
            "technicianId" to technicianId,
            "status" to status,
            "estimatedCost" to estimatedCost,
            "scheduledDate" to scheduledDate,
            "completedDate" to completedDate,
            "location" to location,
            "createdAt" to createdAt
        )
    }
}