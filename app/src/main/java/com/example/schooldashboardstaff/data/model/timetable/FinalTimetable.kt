package com.example.schooldashboardstaff.data.model.timetable

data class FinalTimetable(
    val classSchedules: Map<String, TimetableGrid>,
    val teacherSchedules: Map<String, TimetableGrid>
)

