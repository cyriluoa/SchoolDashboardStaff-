package com.example.schooldashboardstaff.data.model

data class Subject(
    val id: String = "", // Firestore doc ID
    val name: String = "", // e.g., "PHYSICS"
    val displayName: String = "" // e.g., "Physics"
)
