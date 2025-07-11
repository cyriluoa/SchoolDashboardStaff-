package com.example.schooldashboardstaff.data.model

import com.example.schooldashboardstaff.R

enum class SchoolClassState(val colorResId: Int, val label: String) {
    UNSTABLE_SUBJECTS_NOT_ASSIGNED(R.color.primary_red, "Unstable: 0 subjects assigned"),
    UNSTABLE_NO_PERIODS_LEFT(R.color.purple, "Unstable: No periods left"),
    UNSTABLE_CLASS_TEACHER_ASSIGNED(R.color.blue, "Unstable: Class teacher assigned"),
    UNSTABLE_SUBJECTS_ASSIGNED(R.color.yellow, "Unstable: Subjects assigned "),
    STABLE_STUDENTS_PRESENT(R.color.green, "Stable: Students assigned"),
    STABLE_CLASS_FULL(R.color.black, "Stable: Class full")
}
