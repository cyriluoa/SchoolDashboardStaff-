package com.example.schooldashboardstaff.data.model



data class SchoolClass(
    val id: String = "",
    val schoolId: String = "",

    val grade: Int = 0,
    val section: String = "",

    val classTeacherId: String? = null,
    val subjectAssignments: Map<String, String> = emptyMap(),
    val studentIds: List<String> = emptyList(),

    val maxSubjects: Int = 0,
    val maxStudents: Int = 0,

    val createdAt: Long = System.currentTimeMillis()
) {
    val isStable: Boolean
        get() = classTeacherId != null &&
                subjectAssignments.size == maxSubjects &&
                studentIds.isNotEmpty()

    val isFull: Boolean
        get() = studentIds.size >= maxStudents
}
