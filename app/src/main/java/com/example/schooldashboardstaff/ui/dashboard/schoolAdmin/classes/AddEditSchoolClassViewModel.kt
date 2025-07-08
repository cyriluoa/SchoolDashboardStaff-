package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.classes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schooldashboardstaff.data.model.SchoolClass
import com.example.schooldashboardstaff.data.repository.SchoolClassRepository

class AddEditSchoolClassViewModel : ViewModel() {

    private val _addStatus = MutableLiveData<Result<Unit>>()
    val addStatus: LiveData<Result<Unit>> get() = _addStatus

    fun addSchoolClass(
        schoolId: String,
        grade: Int,
        maxStudents: Int,
        maxSubjects: Int
    ) {
        val repo = SchoolClassRepository(schoolId)
        val schoolClass = SchoolClass(
            schoolId = schoolId,
            grade = grade,
            maxStudents = maxStudents,
            maxSubjects = maxSubjects
            // section handled by repo
        )

        repo.addClass(
            schoolClass,
            onSuccess = { _addStatus.value = Result.success(Unit) },
            onFailure = { e -> _addStatus.value = Result.failure(e) }
        )
    }
}
