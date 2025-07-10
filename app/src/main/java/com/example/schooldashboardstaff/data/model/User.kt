package com.example.schooldashboardstaff.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class User(
    val uid: String = "",
    val firstname: String = "",
    val lastname: String = "",
    val username: String = "",
    val email: String = "",
    val role: UserRole = UserRole.TEACHER, // default
    val schoolId: String = "",
    val classIds: List<String>? = null,         //Only for teachers and students
    val subjectIds: List<String>? = null,        //Only for teachers and students
    // For teachers only
    val isClassTeacher: Boolean? = null,
    val maxPeriods: Int? = null,
    val assignedPeriods: Int? = null
) : Parcelable
