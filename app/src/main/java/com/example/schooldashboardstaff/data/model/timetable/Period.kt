package com.example.schooldashboardstaff.data.model.timetable

data class Period(
    val classId: String,
    val subjectId: String,
    val teacherId: String,
    val subjectName: String,
    val colorInt: Int
)

typealias TimetableGrid = Array<Array<Period?>> // [5][8] -> 5 days × 8 periods
