package com.example.schooldashboardstaff.data.repository

import com.example.schooldashboardstaff.data.firebase.AssignManager
import com.example.schooldashboardstaff.data.model.Subject
import javax.inject.Inject

class AssignRepository @Inject constructor(
    private val assignManager: AssignManager
) {
    suspend fun assignSubjectsToClass(
        schoolId: String,
        classId: String,
        selectedSubjects: List<Subject>,
        periodsLeft: Int
    ) {
        assignManager.assignSubjectsToClass(schoolId, classId, selectedSubjects, periodsLeft)
    }
}
