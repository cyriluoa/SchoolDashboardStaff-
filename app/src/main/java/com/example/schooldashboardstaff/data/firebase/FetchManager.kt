package com.example.schooldashboardstaff.data.firebase

import android.util.Log
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

        val dbRef = db.collection(Constants.SCHOOLS_COLLECTION)
            .document(schoolId)
            .collection(Constants.SUBJECTS_COLLECTION)

        val chunks = subjectIds.chunked(10) // Firestore allows max 10 values in whereIn
        val allSubjects = mutableListOf<Subject>()
        var completed = 0
        var hasError = false

        chunks.forEach { chunk ->
            dbRef.whereIn("id", chunk)
                .get()
                .addOnSuccessListener { snapshot ->
                    synchronized(allSubjects) {
                        allSubjects += snapshot.toObjects(Subject::class.java)
                        completed++
                        if (completed == chunks.size && !hasError) {
                            Log.d("getSubjectsByIds", "Fetched total ${allSubjects.size} subjects successfully.")
                            onSuccess(allSubjects)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    if (!hasError) {
                        hasError = true
                        Log.e("getSubjectsByIds", "Failed to fetch subjects chunk: $chunk", e)
                        onFailure(e)
                    }
                }
        }
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

        val chunks = userIds.chunked(10)
        val allUsers = mutableListOf<User>()
        var completed = 0
        var hasError = false

        chunks.forEach { chunk ->
            db.collection(Constants.USERS_COLLECTION)
                .whereIn("uid", chunk)
                .get()
                .addOnSuccessListener { snapshot ->
                    synchronized(allUsers) {
                        allUsers += snapshot.toObjects(User::class.java)
                        completed++
                        if (completed == chunks.size && !hasError) {
                            Log.d("getUsersByIds", "Fetched total ${allUsers.size} users successfully.")
                            onSuccess(allUsers)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    if (!hasError) {
                        hasError = true
                        Log.e("getUsersByIds", "Failed to fetch user chunk: $chunk", e)
                        onFailure(e)
                    }
                }
        }
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

