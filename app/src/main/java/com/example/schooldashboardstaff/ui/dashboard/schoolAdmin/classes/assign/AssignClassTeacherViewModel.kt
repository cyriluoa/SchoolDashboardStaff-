package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.classes.assign

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schooldashboardstaff.data.model.SchoolClass
import com.example.schooldashboardstaff.data.model.User
import com.example.schooldashboardstaff.data.repository.AssignRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class AssignClassTeacherViewModel @Inject constructor(
    private val repository: AssignRepository
) : ViewModel() {

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    fun assignClassTeacher(
        schoolId: String,
        schoolClass: SchoolClass,
        teacher: User
    ) {
        viewModelScope.launch {
            try {
                repository.assignClassTeacherToClassAndUser(
                    schoolId = schoolId,
                    classId = schoolClass.id,
                    teacherId = teacher.uid
                )
                _message.value = "✅ Assigned ${teacher.username} as class teacher for Grade ${schoolClass.grade}-${schoolClass.section}"
            } catch (e: Exception) {
                _message.value = "❌ Failed to assign class teacher. Please try again."
            }
        }
    }
}

