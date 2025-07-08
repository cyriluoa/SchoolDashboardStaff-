package com.example.schooldashboardstaff.data.repository

import com.example.schooldashboardstaff.data.firebase.SubjectManager
import com.example.schooldashboardstaff.data.model.Subject

class SubjectRepository(private val schoolId: String) {

    private val subjectManager = SubjectManager(schoolId)

    fun addSubject(
        subject: Subject,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        subjectManager.addSubject(subject, onSuccess, onFailure)
    }
}
