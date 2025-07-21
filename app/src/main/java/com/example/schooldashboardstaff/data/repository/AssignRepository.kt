package com.example.schooldashboardstaff.data.repository

import android.util.Log
import com.example.schooldashboardstaff.data.firebase.AssignManager
import com.example.schooldashboardstaff.data.model.SchoolClass
import com.example.schooldashboardstaff.data.model.Subject
import javax.inject.Inject

class AssignRepository @Inject constructor(
    private val assignManager: AssignManager
) {
    suspend fun assignSubjectsToClass(
        schoolId: String,
        classId: String,
        selectedSubjects: List<Subject>,
        periodsLeft: Int,
        assignedSubjects : Set<String>
    ) {
        assignManager.assignSubjectsToClass(schoolId, classId, selectedSubjects, periodsLeft, assignedSubjects)
    }

    suspend fun assignTeachersToSubjects(
        schoolClass: SchoolClass,
        updatedAssignments: Map<String, String>,
        subjects: List<Subject>
    ) {
        try {
//            Log.d("AssignManager", "Step 1: Updating users for assignments")
            assignManager.updateUsersForAssignments(
                schoolId = schoolClass.schoolId,
                schoolClassId = schoolClass.id,
                updatedAssignments = updatedAssignments,
                subjects = subjects
            )

//            Log.d("AssignManager", "Step 2: Updating class assignments")
            assignManager.updateClassAssignments(
                schoolClass = schoolClass,
                updatedAssignments = updatedAssignments
            )

//            Log.d("AssignManager", "✅ Both updates done")
        } catch (e: Exception) {
            Log.e("AssignManager", "❌ assignTeachersToSubjects failed", e)
            throw e
        }
    }

    suspend fun assignClassTeacherToClassAndUser(
        schoolId: String,
        classId: String,
        teacherId: String
    ) {
        try {
            assignManager.updateClassTeacherId(schoolId, classId, teacherId)
            assignManager.markUserAsClassTeacher(teacherId)
        } catch (e: Exception) {
            Log.e("AssignRepo", "Failed to assign class teacher", e)
            throw e
        }
    }





}
