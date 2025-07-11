package com.example.schooldashboardstaff.data.repository


import com.example.schooldashboardstaff.data.firebase.SearchManager
import com.example.schooldashboardstaff.data.model.Subject
import javax.inject.Inject

class SearchRepository @Inject constructor(
    private val searchManager: SearchManager
) {
    suspend fun getUnassignedSubjectsForGrade(
        schoolId: String,
        grade: Int,
        assignedSubjectIds: Set<String>
    ): List<Subject> {
        val allSubjects = searchManager.getSubjectsForGrade(schoolId, grade)
        return allSubjects.filterNot { it.id in assignedSubjectIds }
    }


    // You can later add:
    // suspend fun getClassesForGrade(...)
    // suspend fun getTeachersForSubject(...)
    // etc.
}
