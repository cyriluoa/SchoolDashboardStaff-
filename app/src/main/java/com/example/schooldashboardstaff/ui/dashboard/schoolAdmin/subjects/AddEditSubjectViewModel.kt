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



    /**
     * Adds a subject to Firestore using the provided schoolId.
     */
    fun addSubject(subject: Subject, schoolId: String) {
        val repository = SubjectRepository(schoolId)

        _isLoading.value = true
        repository.addSubject(
            subject,
            onSuccess = {
                _isLoading.value = false
                _statusMessage.value = "Subject added successfully!"
            },
            onFailure = {
                _isLoading.value = false
                _statusMessage.value = "Failed to add subject: ${it.message}"
            }
        )
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }
}
