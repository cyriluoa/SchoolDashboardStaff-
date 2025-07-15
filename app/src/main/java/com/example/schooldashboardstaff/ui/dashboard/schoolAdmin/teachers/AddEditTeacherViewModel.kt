package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.teachers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schooldashboardstaff.data.model.User
import com.example.schooldashboardstaff.data.repository.SchoolRepository
import com.example.schooldashboardstaff.data.repository.TeacherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class AddEditTeacherViewModel @Inject constructor(
    private val schoolRepository: SchoolRepository,
    private val teacherRepository: TeacherRepository
) : ViewModel() {

    private val _isUsernameAvailable = MutableLiveData<Boolean?>()
    val isUsernameAvailable: LiveData<Boolean?> = _isUsernameAvailable

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _statusMessage = MutableLiveData<String?>()
    val statusMessage: LiveData<String?> = _statusMessage

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

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    fun createTeacherUser(
        email: String,
        password: String,
        user: User
    ) {
        _isLoading.value = true

        teacherRepository.createTeacherUser(
            email = email,
            password = password,
            user = user,
            onSuccess = {
                _isLoading.postValue(false)
                _statusMessage.postValue("✅ Teacher created successfully")
            },
            onFailure = { exception ->
                _isLoading.postValue(false)
                _statusMessage.postValue("❌ Failed: ${exception.message}")
            }
        )
    }
}
