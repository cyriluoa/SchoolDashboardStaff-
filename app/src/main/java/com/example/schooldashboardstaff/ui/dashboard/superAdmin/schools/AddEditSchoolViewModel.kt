package com.example.schooldashboardstaff.ui.dashboard.superAdmin.schools


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schooldashboardstaff.data.firebase.UserManager
import com.example.schooldashboardstaff.data.model.School
import com.example.schooldashboardstaff.data.repository.SchoolRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class AddEditSchoolViewModel @Inject constructor(
    private val schoolRepository: SchoolRepository
): ViewModel() {

    private val _isUsernameAvailable = MutableLiveData<Boolean?>()
    val isUsernameAvailable: LiveData<Boolean?> = _isUsernameAvailable
    val registrationStatus = MutableLiveData<Result<Unit>>() // success/failure status

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

    fun registerSchoolWithAdmin(
        schoolName: String,
        location: String,
        startingGrade: Int,
        finalGrade: Int,
        adminFirstName: String,
        adminLastName: String,
        adminEmail: String,
        adminUsername: String,
        adminPassword: String
    ) {
        val school = School(
            name = schoolName,
            location = location,
            startingGrade = startingGrade,
            finalGrade = finalGrade
        )

        schoolRepository.createSchoolWithAdmin(
            school = school,
            adminEmail = adminEmail,
            adminPassword = adminPassword,
            adminFirstName = adminFirstName,
            adminLastName = adminLastName,
            adminUsername = adminUsername,
            onSuccess = {
                registrationStatus.value = Result.success(Unit)
            },
            onFailure = { exception ->
                registrationStatus.value = Result.failure(exception)
            }
        )
    }

    fun assignAdminToSchool(
        schoolId: String,
        adminFirstName: String,
        adminLastName: String,
        adminEmail: String,
        adminUsername: String,
        adminPassword: String
    ) {
        schoolRepository.assignAdminToSchool(
            schoolId = schoolId,
            adminEmail = adminEmail,
            adminPassword = adminPassword,
            adminFirstName = adminFirstName,
            adminLastName = adminLastName,
            adminUsername = adminUsername,
            onSuccess = {
                registrationStatus.value = Result.success(Unit)
            },
            onFailure = { exception ->
                registrationStatus.value = Result.failure(exception)
            }
        )
    }

    fun updateSchoolDetails(
        schoolId: String,
        schoolName: String,
        location: String,
        startingGrade: Int,
        finalGrade: Int
    ) {
        schoolRepository.updateSchoolDetails(
            schoolId = schoolId,
            name = schoolName,
            location = location,
            startingGrade = startingGrade,
            finalGrade = finalGrade,
            onSuccess = {
                registrationStatus.value = Result.success(Unit)
            },
            onFailure = { exception ->
                registrationStatus.value = Result.failure(exception)
            }
        )
    }


}
