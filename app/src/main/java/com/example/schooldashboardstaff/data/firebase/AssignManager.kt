package com.example.schooldashboardstaff.data.firebase


import com.example.schooldashboardstaff.data.model.Subject
import com.example.schooldashboardstaff.utils.Constants
import com.google.firebase.firestore.FieldValue
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
}

