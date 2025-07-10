package com.example.schooldashboardstaff.data.repository


import com.example.schooldashboardstaff.data.firebase.SearchManager
import com.example.schooldashboardstaff.data.model.Subject
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val searchManager: SearchManager
) {
    suspend fun getSubjectsForGrade(schoolId: String, grade: Int): List<Subject> {
        return searchManager.getSubjectsForGrade(schoolId, grade)
    }

    // You can later add:
    // suspend fun getClassesForGrade(...)
    // suspend fun getTeachersForSubject(...)
    // etc.
}
