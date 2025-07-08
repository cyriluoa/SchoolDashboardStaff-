package com.example.schooldashboardstaff.data.model

import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.example.schooldashboardstaff.R

data class Subject(
    val id: String = "",
    val name: String = "",
    val displayName: String = "",
    val grade: Int = 0,
    val teacherIds: List<String> = emptyList(),
    val classIds: List<String> = emptyList(),
    @ColorRes
    val colorResId: Int = R.color.primary_red

)
