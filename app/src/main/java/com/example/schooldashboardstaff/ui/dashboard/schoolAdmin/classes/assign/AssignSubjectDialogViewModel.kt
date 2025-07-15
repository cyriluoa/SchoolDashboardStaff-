package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.classes.assign

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schooldashboardstaff.data.model.Subject
import com.example.schooldashboardstaff.data.repository.AssignRepository
import com.example.schooldashboardstaff.data.repository.FetchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class AssignSubjectDialogViewModel @Inject constructor(
    private val fetchRepository: FetchRepository
) : ViewModel() {

    private val _subjects = MutableLiveData<List<Subject>>()
    val subjects: LiveData<List<Subject>> get() = _subjects

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
}

