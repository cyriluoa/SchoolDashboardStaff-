package com.example.schooldashboardstaff.data.model.timetable
data class FinalSerializableTimetable(
    val classSchedules: Map<String, FlatTimetableGrid>,
    val teacherSchedules: Map<String, FlatTimetableGrid>
)

