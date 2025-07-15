package com.example.schooldashboardstaff.data.firebase

import com.example.schooldashboardstaff.data.model.Subject
import com.example.schooldashboardstaff.utils.Constants
import jakarta.inject.Inject



class FetchManager @Inject constructor() : FirestoreManager() {

    fun getSubjectsByIds(
        schoolId: String,
        subjectIds: List<String>,
        onSuccess: (List<Subject>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (subjectIds.isEmpty()) {
            onSuccess(emptyList())
            return
        }

        db.collection(Constants.SCHOOLS_COLLECTION)
            .document(schoolId)
            .collection(Constants.SUBJECTS_COLLECTION)
            .whereIn("id", subjectIds)
            .get()
            .addOnSuccessListener { snapshot ->
                val subjects = snapshot.toObjects(Subject::class.java)
                onSuccess(subjects)
            }
            .addOnFailureListener { onFailure(it) }
    }
}

