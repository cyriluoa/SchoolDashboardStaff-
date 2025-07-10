package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.search.subjects

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schooldashboardstaff.data.model.Subject
import com.example.schooldashboardstaff.data.repository.AssignRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class AssignSubjectViewModel @Inject constructor(
    private val assignRepository: AssignRepository
) : ViewModel() {

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _assignmentSuccess = MutableLiveData<Boolean>()
    val assignmentSuccess: LiveData<Boolean> get() = _assignmentSuccess

    fun assignSubjectsToClass(
        schoolId: String,
        classId: String,
        selectedSubjects: List<Subject>,
        periodsLeft: Int
    ) {
        viewModelScope.launch {
            try {
                assignRepository.assignSubjectsToClass(
                    schoolId,
                    classId,
                    selectedSubjects,
                    periodsLeft
                )
                _assignmentSuccess.value = true
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Assignment failed"
            }
        }
    }

    // Optional: Clear status after observing
    fun clearMessages() {
        _errorMessage.value = null
        _assignmentSuccess.value = false
    }
}
