package com.example.schooldashboardstaff.data.model

import android.graphics.Color
import android.os.Parcelable
import androidx.annotation.ColorInt
import kotlinx.parcelize.Parcelize

@Parcelize
data class Subject(
    val id: String = "",
    val name: String = "",
    val displayName: String = "",
    val grade: Int = 0,
    val teacherIds: List<String> = emptyList(),
    val classIds: List<String> = emptyList(),
    val periodCount: Int = 0,
    @ColorInt
    val colorInt: Int = Color.RED
) : Parcelable
