package com.example.schooldashboardstaff.data.model.timetable

data class TimetableDocument(
    val id: String = "",
    val draftName: String = "",
    val isActive: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val timetable: FinalSerializableTimetable

)
