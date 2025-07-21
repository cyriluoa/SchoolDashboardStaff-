package com.example.schooldashboardstaff.data.repository

import com.example.schooldashboardstaff.data.firebase.FetchManager
import com.example.schooldashboardstaff.data.model.DisplaySubjectTeacher
import com.example.schooldashboardstaff.data.model.SchoolClass
import com.example.schooldashboardstaff.data.model.Subject
import com.example.schooldashboardstaff.data.model.User
import jakarta.inject.Inject


class FetchRepository @Inject constructor(
    private val fetchManager: FetchManager
) {
    fun getSubjectsByIds(
        schoolId: String,
        subjectIds: List<String>,
        onSuccess: (List<Subject>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        fetchManager.getSubjectsByIds(schoolId, subjectIds, onSuccess, onFailure)
    }

    fun getUsersByIds(
        userIds: List<String>,
        onSuccess: (List<User>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        fetchManager.getUsersByIds(userIds, onSuccess, onFailure)
    }

    fun getSchoolClassDetails(
        schoolClass: SchoolClass,
        onSuccess: (List<DisplaySubjectTeacher>, List<User>, User?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val subjectIds = schoolClass.subjectAssignments.keys.toList()
        val teacherIds = schoolClass.subjectAssignments.values.toMutableSet()

        val studentIds = schoolClass.studentIds

        getSubjectsByIds(
            schoolId = schoolClass.schoolId,
            subjectIds = subjectIds,
            onSuccess = { subjects ->

                getUsersByIds(
                    userIds = teacherIds.toList(),
                    onSuccess = { teachers ->
                        val subjectMap = subjects.associateBy { it.id }
                        val teacherMap = teachers.associateBy { it.uid }

                        val displaySubjectTeachers = schoolClass.subjectAssignments.mapNotNull { (subjectId, teacherId) ->
                            val subject = subjectMap[subjectId]
                            val teacher = teacherMap[teacherId]
                            if (subject != null && teacher != null) {
                                DisplaySubjectTeacher(subject, teacher)
                            } else null
                        }

                        val classTeacher = schoolClass.classTeacherId?.let { teacherMap[it] }

                        getUsersByIds(
                            userIds = studentIds,
                            onSuccess = { students ->
                                onSuccess(displaySubjectTeachers, students, classTeacher)
                            },
                            onFailure = {
                                onSuccess(displaySubjectTeachers, emptyList(), classTeacher)
                            }
                        )
                    },
                    onFailure = onFailure
                )
            },
            onFailure = onFailure
        )
    }
}

