package com.example.schooldashboardstaff.data.model.timetable

data class SubjectPlacementPlan(
    val classId: String,
    val subjectId: String,
    val teacherId: String,
    val subjectName: String,
    val periodCount: Int,
    val colorInt: Int,
    val perDayDistribution: List<Int>
)
