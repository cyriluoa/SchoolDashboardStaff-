package com.example.schooldashboardstaff.data.model.timetable

data class SubjectPlacementPlan(
    val classId: String,
    val className: String,
    val subjectId: String,
    val subjectName: String,
    val teacherId: String,
    val teacherUsername: String,
    val teacherFullName: String,
    val colorInt: Int,
    val periodCount: Int,
    val perDayDistribution: List<Int>
)
