package com.example.schooldashboardstaff.data.repository

import com.example.schooldashboardstaff.data.firebase.TimetableManager
import com.example.schooldashboardstaff.data.model.timetable.TimetableDocument
import jakarta.inject.Inject

class TimetableRepository @Inject constructor(
    private val timetableManager: TimetableManager
) {
    fun saveDraftTimetable(schoolId: String, document: TimetableDocument, onResult: (Boolean) -> Unit) {
        timetableManager.saveTimetable(schoolId, document, onResult)
    }
}
