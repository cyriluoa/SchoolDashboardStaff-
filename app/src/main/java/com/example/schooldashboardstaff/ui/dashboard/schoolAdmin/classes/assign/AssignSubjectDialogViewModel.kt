package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.classes.assign

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schooldashboardstaff.data.model.SchoolClass
import com.example.schooldashboardstaff.data.model.Subject
import com.example.schooldashboardstaff.data.repository.AssignRepository
import com.example.schooldashboardstaff.data.repository.FetchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class AssignSubjectDialogViewModel @Inject constructor(
    private val fetchRepository: FetchRepository,
    private val assignRepository: AssignRepository
) : ViewModel() {

    private val _subjects = MutableLiveData<List<Subject>>()
    val subjects: LiveData<List<Subject>> get() = _subjects

    private val _assignmentSuccess = MutableLiveData<Boolean>()
    val assignmentSuccess: LiveData<Boolean> get() = _assignmentSuccess

    fun fetchSubjectsForClass(schoolId: String, subjectIds: List<String>) {
        fetchRepository.getSubjectsByIds(
            schoolId = schoolId,
            subjectIds = subjectIds,
            onSuccess = { result ->
                _subjects.postValue(result)
            },
            onFailure = { error ->
                Log.e("AssignSubjectDialogVM", "Failed to load subjects", error)
                _subjects.postValue(emptyList())
            }
        )
    }



    fun assignTeachersToSubjects(
        schoolClass: SchoolClass,
        updatedAssignments: Map<String, String>,
        subjects: List<Subject>
    ) {
        viewModelScope.launch {
            try {
                assignRepository.assignTeachersToSubjects(
                    schoolClass = schoolClass,
                    updatedAssignments = updatedAssignments,
                    subjects = subjects
                )
//                Log.d("AssignSubjectVM", "Teacher assignment successful")
                _assignmentSuccess.postValue(true)
            } catch (e: Exception) {
                Log.e("AssignSubjectVM", "Assignment failed", e)
                _assignmentSuccess.postValue(false)
            }
        }
    }

}

