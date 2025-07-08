package com.example.schooldashboardstaff.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class School(
    val id: String = "",
    val name: String = "",
    val location: String = "",
    val createdBy: String = "",
    val adminIds: MutableList<String> = mutableListOf(),
    val startingGrade: Int = 0,
    val finalGrade: Int = 0,
    val createdAt: com.google.firebase.Timestamp? = null,
    val isActive: Boolean = true,
    val numberOfStudents: Int = 0,
    val numberOfTeachers: Int = 0,
    val numberOfClasses: Int = 0
): Parcelable {

}

