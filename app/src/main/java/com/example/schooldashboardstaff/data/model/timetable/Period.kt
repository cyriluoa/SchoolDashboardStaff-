package com.example.schooldashboardstaff.data.model.timetable

data class Period(
    val classId: String,
    val className: String,           // 🔹 e.g., "Grade 5 - A"
    val subjectId: String,
    val subjectName: String,
    val colorInt: Int,
    val teacherId: String,
    val teacherUsername: String,
    val teacherFullName: String      // 🔹 First + Last
)


typealias TimetableGrid = Array<Array<Period?>> // [5][8] -> 5 days × 8 periods
