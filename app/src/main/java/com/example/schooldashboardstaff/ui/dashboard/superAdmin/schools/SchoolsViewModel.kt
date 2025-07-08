package com.example.schooldashboardstaff.ui.dashboard.superAdmin.schools

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schooldashboardstaff.data.model.DisplaySchool
import com.example.schooldashboardstaff.data.model.School
import com.example.schooldashboardstaff.data.repository.SchoolRepository

class SchoolsViewModel(
    private val schoolRepository: SchoolRepository = SchoolRepository()
) : ViewModel() {

    private val _displaySchools = MutableLiveData<List<DisplaySchool>>()
    val displaySchools: LiveData<List<DisplaySchool>> = _displaySchools


    fun listenToSchools() {
        schoolRepository.listenToSchoolsWithAdminUsernames(
            onSuccess = { schools, usernameMap ->
                val displayList = schools.map { school ->
                    val usernames = school.adminIds.mapNotNull { usernameMap[it] }
                    DisplaySchool(school = school, adminUsernames = usernames)
                }
                _displaySchools.value = displayList
            },
            onFailure = {
                Log.e("SchoolsViewModel", "Failed to fetch schools", it)
            }
        )
    }

    fun removeSchoolsListener() {
        schoolRepository.removeSchoolsListener()
    }



    fun deleteSchoolWithAdmins(school: School) {
        schoolRepository.deleteSchoolAndAdmins(
            school,
            onSuccess = {
                Log.d("SchoolsViewModel", "School and admins deleted successfully")
            },
            onFailure = {
                Log.e("SchoolsViewModel", "Failed to delete school", it)
            }
        )
    }



    override fun onCleared() {
        super.onCleared()
        removeSchoolsListener()
    }



//      fun fetchSchools() {
//            schoolRepository.getAllSchoolsWithAdminUsernames(
//                onSuccess = { schoolList, usernameMap ->
//                    _schools.value = schoolList
//                    _adminUsernameMap.value = usernameMap
//                },
//                onFailure = {
//                    Log.e("SchoolsViewModel", "Failed to fetch schools", it)
//                }
//            )
//        }

}
