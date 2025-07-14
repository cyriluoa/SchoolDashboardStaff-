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


    suspend fun getUnassignedSubjectsForTeacher(
        schoolId: String,
        assignedSubjectIds: Set<String>
    ): List<Subject> {
        return searchManager.getUnassignedSubjectsForTeacher(
            schoolId = schoolId,
            assignedSubjectIds = assignedSubjectIds
        )
    }

    suspend fun getAllSubjects(schoolId: String): List<Subject> {
        return searchManager.getAllSubjects(schoolId)
    }

    // You can later add:
    // suspend fun getClassesForGrade(...)
    // suspend fun getTeachersForSubject(...)
    // etc.
}
