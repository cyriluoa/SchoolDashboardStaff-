package com.example.schooldashboardstaff.data.model.timetable

data class TimetableInput(
    val classInfoMap: Map<String, ClassInfo>,
    val subjectInfoMap: Map<String, SubjectInfo>,
    val teacherInfoMap: Map<String, TeacherInfo>
)
