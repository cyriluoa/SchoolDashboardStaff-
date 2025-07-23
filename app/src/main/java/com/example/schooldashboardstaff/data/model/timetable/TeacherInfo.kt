package com.example.schooldashboardstaff.data.model.timetable

data class TeacherInfo(
    val id: String,
    val username: String,
    val fullName: String,  // "John Smith"
    val assignedPeriods: Int,
    val maxPerDay: Int
)
