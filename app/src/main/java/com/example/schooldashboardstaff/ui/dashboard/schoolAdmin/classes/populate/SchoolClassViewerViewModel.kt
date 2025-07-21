package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.classes.populate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schooldashboardstaff.data.model.DisplaySubjectTeacher
import com.example.schooldashboardstaff.data.model.SchoolClass
import com.example.schooldashboardstaff.data.model.User
import com.example.schooldashboardstaff.data.repository.FetchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class SchoolClassViewerViewModel @Inject constructor(
    private val fetchRepository: FetchRepository
) : ViewModel() {

    private val _classWithDetails = MutableLiveData<Triple<SchoolClass, List<DisplaySubjectTeacher>, List<User>>>()
    val classWithDetails: LiveData<Triple<SchoolClass, List<DisplaySubjectTeacher>, List<User>>> = _classWithDetails

    var classTeacherName: String? = null

    fun loadClassDetails(schoolClass: SchoolClass) {
        fetchRepository.getSchoolClassDetails(
            schoolClass = schoolClass,
            onSuccess = { displaySubjects, students, classTeacher->
                classTeacherName = classTeacher?.fullName
                _classWithDetails.value = Triple(schoolClass, displaySubjects, students)
            },
            onFailure = {
                _classWithDetails.value = Triple(schoolClass, emptyList(), emptyList())
            }
        )
    }
}

