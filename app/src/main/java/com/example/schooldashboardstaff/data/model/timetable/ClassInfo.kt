package com.example.schooldashboardstaff.data.model.timetable

data class ClassInfo(
    val id: String,
    val name: String, // e.g., "Grade 5 - A"
    val subjectAssignments: Map<String, String>,
    val maxPeriods: Int = 40
)

