package com.example.schooldashboardstaff.data.firebase

import com.example.schooldashboardstaff.data.model.Subject
import com.example.schooldashboardstaff.utils.Constants

class SubjectManager(private val schoolId: String) : FirestoreManager() {

    private val subjectsRef = db.collection(Constants.SCHOOLS_COLLECTION)
        .document(schoolId)
        .collection(Constants.SUBJECTS_COLLECTION)

    /**
     * Adds a new subject to the current school.
     * If ID is empty, generates one automatically.
     */
    fun addSubject(
        subject: Subject,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val docRef = if (subject.id.isNotEmpty()) {
            subjectsRef.document(subject.id)
        } else {
            subjectsRef.document()
        }

        val subjectWithId = subject.copy(id = docRef.id)

        docRef.set(subjectWithId)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
}
