package com.example.schooldashboardstaff.data.model

import android.graphics.Color
import androidx.annotation.ColorInt


data class Subject(
    val id: String = "",
    val name: String = "",
    val displayName: String = "",
    val grade: Int = 0,
    val teacherIds: List<String> = emptyList(),
    val classIds: List<String> = emptyList(),
    @ColorInt
    val colorInt: Int = Color.RED


)
