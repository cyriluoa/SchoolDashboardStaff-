package com.example.schooldashboardstaff.data.firebase

import com.example.schooldashboardstaff.data.model.SchoolClass
import com.example.schooldashboardstaff.data.model.Subject
import com.example.schooldashboardstaff.data.model.User
import com.example.schooldashboardstaff.utils.Constants
import com.google.firebase.firestore.ListenerRegistration
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

    fun getUsersByIds(
        userIds: List<String>,
        onSuccess: (List<User>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (userIds.isEmpty()) {
            onSuccess(emptyList())
            return
        }

        db.collection(Constants.USERS_COLLECTION)
            .whereIn("uid", userIds)
            .get()
            .addOnSuccessListener { snapshot ->
                val users = snapshot.toObjects(User::class.java)
                onSuccess(users)
            }
            .addOnFailureListener(onFailure)
    }

    fun listenToSchoolClasses(
        schoolId: String,
        onSuccess: (List<SchoolClass>) -> Unit,
        onFailure: (Exception) -> Unit
    ): ListenerRegistration {
        return db.collection(Constants.SCHOOLS_COLLECTION)
            .document(schoolId)
            .collection(Constants.SCHOOL_CLASSES_COLLECTION)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onFailure(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val classes = snapshot.toObjects(SchoolClass::class.java)
                    onSuccess(classes)
                }
            }
    }

}

