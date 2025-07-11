package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.subjects

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schooldashboardstaff.data.model.Subject
import com.example.schooldashboardstaff.data.repository.SubjectRepository

class AddEditSubjectViewModel : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _statusMessage = MutableLiveData<String?>()
    val statusMessage: LiveData<String?> = _statusMessage

    private var repository: SubjectRepository? = null

    fun addSubjectsBatch(subjects: List<Subject>, schoolId: String) {
        if (subjects.isEmpty()) {
            _statusMessage.value = "No subjects to add"
            return
        }

        _isLoading.value = true
        if (repository == null) {
            repository = SubjectRepository(schoolId)
        }

        repository?.addSubjectsBatch(
            subjects,
            onSuccess = {
                _isLoading.value = false
                _statusMessage.value = "Subjects added successfully!"
            },
            onFailure = { exception ->
                _isLoading.value = false
                _statusMessage.value = "Failed to add subjects: ${exception.message}"
            }
        )
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }
}
