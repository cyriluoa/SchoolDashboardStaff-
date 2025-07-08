package com.example.schooldashboardstaff.data.model.input

data class MissingGradeInput(
    val grade: Int,
    var numSections: Int = 1,
    val sectionInputs: MutableList<SectionInput> = mutableListOf(),
    var isCompleted: Boolean = false
)
