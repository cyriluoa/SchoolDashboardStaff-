package com.example.schooldashboardstaff.data.model

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
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
    val createdBy: String = "",
    val classIds: List<String>? = null,         //Only for teachers and students
    val subjectToClassMap: Map<String, List<String>>? = null, // subjectId → classId
    // For teachers only
    @get:PropertyName("isClassTeacher")
    @set:PropertyName("isClassTeacher")
    var isClassTeacher: Boolean? = null,
    val maxPeriods: Int? = null,
    val assignedPeriods: Int? = null
) : Parcelable{

    val fullName : String
        get() = firstname + " " + lastname
}


