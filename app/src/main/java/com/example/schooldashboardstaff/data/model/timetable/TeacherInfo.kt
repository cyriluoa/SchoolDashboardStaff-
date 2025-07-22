package com.example.schooldashboardstaff.data.model.timetable

data class TeacherInfo(
    val id: String,
    val username: String,
    val maxPerDay: Int, // assignedPeriods / 5 + 1
    val assignedPeriods: Int
)