package com.example.schooldashboardstaff.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class SchoolClass(
    val id: String = "",
    val schoolId: String = "",

    val grade: Int = 0,
    val section: String = "",

    val classTeacherId: String? = null,
    val subjectAssignments: Map<String, String> = emptyMap(),
    val studentIds: List<String> = emptyList(),

    val maxPeriods: Int = 40,
    val maxStudents: Int = 0,
    val periodsLeft : Int = 40,

    val createdAt: Long = System.currentTimeMillis()
) : Parcelable {
    val isStable: Boolean
        get() = classTeacherId != null &&
                periodsLeft == 0 &&
                studentIds.isNotEmpty()



    val isFull: Boolean
        get() = studentIds.size >= maxStudents


    val allTeachersAssigned: Boolean
        get() = subjectAssignments.isNotEmpty() &&
                subjectAssignments.values.none { it.isBlank() }

    val state: SchoolClassState
        get() = when {
            isFull -> SchoolClassState.STABLE_CLASS_FULL
            isStable -> SchoolClassState.STABLE_STUDENTS_PRESENT
            classTeacherId != null -> SchoolClassState.UNSTABLE_CLASS_TEACHER_ASSIGNED
            allTeachersAssigned -> SchoolClassState.UNSTABLE_TEACHERS_ASSIGNED
            periodsLeft == 0 -> SchoolClassState.UNSTABLE_NO_PERIODS_LEFT
            subjectAssignments.isNotEmpty() -> SchoolClassState.UNSTABLE_SUBJECTS_ASSIGNED
            else -> SchoolClassState.UNSTABLE_SUBJECTS_NOT_ASSIGNED
        }

}
