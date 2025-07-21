package com.example.schooldashboardstaff.data.firebase



import android.provider.SyncStateContract
import android.util.Log
import com.example.schooldashboardstaff.data.model.SchoolClass
import com.example.schooldashboardstaff.data.model.Subject
import com.example.schooldashboardstaff.data.model.User
import com.example.schooldashboardstaff.data.model.UserRole
import com.example.schooldashboardstaff.utils.Constants
import com.google.firebase.firestore.FieldPath
import jakarta.inject.Inject
import kotlinx.coroutines.tasks.await

class SearchManager @Inject constructor(): FirestoreManager() {

    suspend fun getSubjectsForGrade(schoolId: String, grade: Int): List<Subject> {
        return try {
            db.collection(Constants.SCHOOLS_COLLECTION)
                .document(schoolId)
                .collection(Constants.SUBJECTS_COLLECTION)
                .whereEqualTo(Constants.SCHOOL_CLASSES_FIELD_GRADE, grade)
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

    suspend fun getTeachersByIds(schoolId: String, ids: List<String>): List<User> {
        if (ids.isEmpty()) return emptyList()

        return try {
            db.collection(Constants.USERS_COLLECTION)
                .whereEqualTo(Constants.USERS_FIELD_SCHOOL_ID, schoolId)
                .whereEqualTo(Constants.USERS_FIELD_ROLE, UserRole.TEACHER.name)
                .whereIn(FieldPath.documentId(), ids)
                .get()
                .await()
                .documents
                .mapNotNull { it.toObject(User::class.java) }
        } catch (e: Exception) {
            Log.e("SearchManager", "Error fetching teachers by IDs", e)
            emptyList()
        }
    }

    suspend fun getClassTeacherCandidates(schoolId: String, schoolClass: SchoolClass): List<User> {
        val userCollection = db.collection(Constants.USERS_COLLECTION)
        val subjectsCollection = db.collection(Constants.SCHOOLS_COLLECTION).document(schoolId).collection(Constants.SUBJECTS_COLLECTION)

        val teacherIdToPeriodCount = mutableMapOf<String, Int>()

        // Step 1: Loop through all subject assignments for the class
        for ((subjectId, teacherId) in schoolClass.subjectAssignments) {
            try {
                val subjectSnap = subjectsCollection.document(subjectId).get().await()
                val periodCount = subjectSnap.getLong("periodCount")?.toInt() ?: 0
                teacherIdToPeriodCount[teacherId] = teacherIdToPeriodCount.getOrDefault(teacherId, 0) + periodCount
            } catch (e: Exception) {
                Log.e("SearchManager", "Error fetching subject $subjectId", e)
            }
        }

        val eligibleTeacherIds = teacherIdToPeriodCount.filter { it.value >= 5 }.keys

        // Step 2: Fetch those users from Firestore
        val candidates = mutableListOf<User>()
        for (teacherId in eligibleTeacherIds) {
            try {
                val snapshot = userCollection.document(teacherId).get().await()
                val user = snapshot.toObject(User::class.java)
                if (user != null && user.role == UserRole.TEACHER && user.isClassTeacher!= null && user.isClassTeacher == false) {
                    candidates.add(user)
                }
            } catch (e: Exception) {
                Log.e("SearchManager", "Error fetching user $teacherId", e)
            }
        }

        return candidates
    }


}
