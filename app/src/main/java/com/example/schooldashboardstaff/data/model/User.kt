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
) : Parcelable
