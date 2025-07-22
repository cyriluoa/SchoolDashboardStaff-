package com.example.schooldashboardstaff.data.model.timetable

data class ClassInfo(
    val id: String,
    val subjectAssignments: Map<String, String>, // subjectId -> teacherId
    val maxPeriods: Int = 40
)
