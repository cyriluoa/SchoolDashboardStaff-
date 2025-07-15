package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.teachers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schooldashboardstaff.data.model.User
import com.example.schooldashboardstaff.data.repository.TeacherRepository
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class TeachersViewModel @Inject constructor(
    private val teacherRepository: TeacherRepository
) : ViewModel() {

    private val _teachers = MutableLiveData<List<User>>()
    val teachers: LiveData<List<User>> = _teachers

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private var listenerRegistration: ListenerRegistration? = null

    fun startListeningToTeachers(schoolId: String) {
        listenerRegistration?.remove()
        listenerRegistration = teacherRepository.listenToTeachers(
            schoolId = schoolId,
            onUpdate = { _teachers.value = it },
            onError = { _errorMessage.value = it }
        )
    }

    fun clearError() {
        _errorMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}
