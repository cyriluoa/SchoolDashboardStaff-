package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.teachers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schooldashboardstaff.data.repository.SchoolRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class AddEditTeacherViewModel @Inject constructor(
    private val schoolRepository: SchoolRepository
): ViewModel() {

    private val _isUsernameAvailable = MutableLiveData<Boolean?>()
    val isUsernameAvailable: LiveData<Boolean?> = _isUsernameAvailable

    fun checkUsernameAvailability(username: String) {
        if (username.isBlank()) {
            _isUsernameAvailable.value = null
            return
        }

        schoolRepository.checkUsernameAvailability(username) { isAvailable ->
            _isUsernameAvailable.value = isAvailable
        }
    }

    fun clearUsernameAvailabilityCheck() {
        _isUsernameAvailable.value = null
    }


}