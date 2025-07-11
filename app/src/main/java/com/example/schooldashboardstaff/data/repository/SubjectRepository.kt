package com.example.schooldashboardstaff.data.repository

import com.example.schooldashboardstaff.data.firebase.SubjectManager
import com.example.schooldashboardstaff.data.model.Subject
import com.google.firebase.firestore.ListenerRegistration

class SubjectRepository(private val schoolId: String) {

    private val subjectManager = SubjectManager(schoolId)

    fun addSubjectsBatch(
        subjects: List<Subject>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        subjectManager.addSubjectsBatch(subjects, onSuccess, onFailure)
    }

    fun listenToSubjects(
        onSubjectsChanged: (List<Subject>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration {
        return subjectManager.listenToSubjects(onSubjectsChanged, onError)
    }
}
