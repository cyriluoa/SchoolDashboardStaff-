package com.example.schooldashboardstaff.data.repository

import com.example.schooldashboardstaff.data.firebase.FetchManager
import com.example.schooldashboardstaff.data.model.Subject
import jakarta.inject.Inject


class FetchRepository @Inject constructor(
    private val fetchManager: FetchManager
) {
    fun getSubjectsByIds(
        schoolId: String,
        subjectIds: List<String>,
        onSuccess: (List<Subject>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        fetchManager.getSubjectsByIds(schoolId, subjectIds, onSuccess, onFailure)
    }
}
