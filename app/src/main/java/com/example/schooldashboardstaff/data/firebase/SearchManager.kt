package com.example.schooldashboardstaff.data.firebase



import com.example.schooldashboardstaff.data.model.Subject
import jakarta.inject.Inject
import kotlinx.coroutines.tasks.await

class SearchManager @Inject constructor(): FirestoreManager() {

    suspend fun getSubjectsForGrade(schoolId: String, grade: Int): List<Subject> {
        return try {
            db.collection("schools")
                .document(schoolId)
                .collection("subjects")
                .whereEqualTo("grade", grade)
                .get()
                .await()
                .toObjects(Subject::class.java)
        } catch (e: Exception) {
            emptyList() // or logError(e), depending on your FirestoreManager
        }
    }
}
