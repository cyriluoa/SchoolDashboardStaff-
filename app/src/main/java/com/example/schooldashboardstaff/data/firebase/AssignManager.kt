package com.example.schooldashboardstaff.data.firebase


import android.util.Log
import com.example.schooldashboardstaff.data.model.SchoolClass
import com.example.schooldashboardstaff.data.model.Subject
import com.example.schooldashboardstaff.utils.Constants
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AssignManager @Inject constructor() : FirestoreManager() {

    suspend fun assignSubjectsToClass(
        schoolId: String,
        classId: String,
        selectedSubjects: List<Subject>,
        periodsLeft: Int,
        assignedSet: Set<String>
    ) {
        val classRef = db.collection(Constants.SCHOOLS_COLLECTION)
            .document(schoolId)
            .collection(Constants.SCHOOL_CLASSES_COLLECTION)
            .document(classId)

        val subjectCollectionRef = db.collection(Constants.SCHOOLS_COLLECTION)
            .document(schoolId)
            .collection(Constants.SUBJECTS_COLLECTION)

        db.runBatch { batch ->
            // ✅ 1. Merge new + old assignments
            val allAssignments = buildMap {
                // Add old ones
                assignedSet.forEach { put(it, "") }
                // Add new ones (overwriting if needed — keys are unique)
                selectedSubjects.forEach { put(it.id, "") }
            }

            batch.update(classRef, mapOf(
                "subjectAssignments" to allAssignments,
                "periodsLeft" to periodsLeft
            ))

            // ✅ 2. Append classId to each subject's classIds
            selectedSubjects.forEach { subject ->
                val subjectRef = subjectCollectionRef.document(subject.id)
                batch.update(subjectRef, "classIds", FieldValue.arrayUnion(classId))
            }
        }.await()
    }

    suspend fun updateClassAssignments(
        schoolClass: SchoolClass,
        updatedAssignments: Map<String, String>
    ) {
        val classRef = db.collection(Constants.SCHOOLS_COLLECTION)
            .document(schoolClass.schoolId)
            .collection(Constants.SCHOOL_CLASSES_COLLECTION)
            .document(schoolClass.id)

        val data = mapOf(
            "subjectAssignments" to updatedAssignments
        )

        classRef.update(data).await()
    }

    suspend fun updateUsersForAssignments(
        schoolId: String,
        schoolClassId: String,
        updatedAssignments: Map<String, String>,
        subjects: List<Subject>
    ) {
        val userCollection = db.collection(Constants.USERS_COLLECTION)
        Log.d("AssignManager", "Preparing to update ${updatedAssignments.size} users")

        try {
            db.runBatch { batch ->
                updatedAssignments.forEach { (subjectId, teacherId) ->
                    if (teacherId.isNotBlank()) {
                        val subject = subjects.find { it.id == subjectId }
                        if (subject == null) {
                            Log.e("AssignManager", "Subject $subjectId not found")
                            return@forEach
                        }

                        val teacherRef = userCollection.document(teacherId)

                        batch.update(
                            teacherRef,
                            "assignedPeriods", FieldValue.increment(subject.periodCount.toLong())
                        )
                        batch.update(
                            teacherRef,
                            FieldPath.of("subjectToClassMap", subjectId),
                            FieldValue.arrayUnion(schoolClassId)
                        )

                        Log.d("AssignManager", "Batching user update: $teacherId for $subjectId")
                    }
                }
            }.await()
        } catch (e: Exception) {
            Log.e("AssignManager", "Batch user update failed", e)
        }

    }

}

