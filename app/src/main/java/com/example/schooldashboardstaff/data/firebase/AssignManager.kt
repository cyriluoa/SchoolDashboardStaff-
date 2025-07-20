package com.example.schooldashboardstaff.data.firebase


import android.util.Log
import com.example.schooldashboardstaff.data.model.SchoolClass
import com.example.schooldashboardstaff.data.model.Subject
import com.example.schooldashboardstaff.data.model.User
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
//        Log.d("AssignManager", "Preparing to update ${updatedAssignments.size} users")

        val updatedTeacherIds = mutableSetOf<String>()

        try {
            // STEP 1: Batch update subjectToClassMap
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
                            FieldPath.of("subjectToClassMap", subjectId),
                            FieldValue.arrayUnion(schoolClassId)
                        )

                        updatedTeacherIds.add(teacherId)
//                        Log.d("AssignManager", "Batching user map update: $teacherId for $subjectId")
                    }
                }
            }.await()

            // STEP 2: Recalculate assignedPeriods per updated teacher
            for (teacherId in updatedTeacherIds) {
                val teacherRef = userCollection.document(teacherId)
                val snapshot = teacherRef.get().await()
                val user = snapshot.toObject(User::class.java)
                Log.d("User",user.toString())

                if (user?.subjectToClassMap != null) {
                    Log.d("Map",user.subjectToClassMap.toString())
                    var totalAssigned = 0
                    for ((subjectId, classList) in user.subjectToClassMap) {
//                        Log.d("Subject ID",subjectId)

                        totalAssigned += (classList.size - 1) * getPeriodCountForSubject(subjectId,schoolId)
//                        Log.d("For Loop","Total Assigned: ${totalAssigned}\nClass List Size: ${classList.size}\nSubject Period Count: ${getPeriodCountForSubject(subjectId,schoolId)}")
                    }
                    teacherRef.update("assignedPeriods", totalAssigned).await()
//                    Log.d("AssignManager", "Updated assignedPeriods=$totalAssigned for $teacherId")
                } else {
                    teacherRef.update("assignedPeriods", 0).await()
//                    Log.d("AssignManager", "Cleared assignedPeriods for $teacherId (no map)")
                }
            }

        } catch (e: Exception) {
            Log.e("AssignManager", "User assignment update failed", e)
        }
    }

    suspend fun getPeriodCountForSubject(subjectId: String, schoolId: String): Int {
        return try {
            val subjectSnap = db.collection(Constants.SCHOOLS_COLLECTION)
                .document(schoolId)
                .collection(Constants.SUBJECTS_COLLECTION)
                .document(subjectId)
                .get()
                .await()

            if (subjectSnap.exists()) {
                subjectSnap.getLong("periodCount")?.toInt() ?: 0
            } else {
                0
            }
        } catch (e: Exception) {
            Log.e("AssignManager", "Failed to fetch periodCount for $subjectId", e)
            0
        }
    }



}

