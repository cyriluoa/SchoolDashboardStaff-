package com.example.schooldashboardstaff.data.model

data class SearchParams(
    val type: String,
    val schoolId: String,
    val classId: String?,
    val grade: Int,
    val periodsLeft: Int,
    val subjectKeys: Array<String>?,
    val user: User?
)
