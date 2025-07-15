package com.example.schooldashboardstaff.data.firebase



import android.provider.SyncStateContract
import com.example.schooldashboardstaff.data.model.Subject
import com.example.schooldashboardstaff.utils.Constants
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

    suspend fun getUnassignedSubjectsForTeacher(
        schoolId: String,
        assignedSubjectIds: Set<String>
    ): List<Subject> {
        return try {
            val allSubjects = db.collection(Constants.SCHOOLS_COLLECTION)
                .document(schoolId)
                .collection(Constants.SUBJECTS_COLLECTION)
                .orderBy(Constants.SCHOOL_CLASSES_FIELD_GRADE)
                .get()
                .await()
                .toObjects(Subject::class.java)

            allSubjects.filterNot { assignedSubjectIds.contains(it.id) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getAllSubjects(schoolId: String): List<Subject> {
        return try {
            db.collection(Constants.SCHOOLS_COLLECTION)
                .document(schoolId)
                .collection(Constants.SUBJECTS_COLLECTION)
                .orderBy(Constants.SCHOOL_CLASSES_FIELD_GRADE)
                .get()
                .await()
                .toObjects(Subject::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
