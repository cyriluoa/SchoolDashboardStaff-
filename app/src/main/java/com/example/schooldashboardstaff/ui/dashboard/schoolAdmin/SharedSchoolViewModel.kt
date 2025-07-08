package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schooldashboardstaff.data.model.School

class SharedSchoolViewModel : ViewModel() {
    private val _school = MutableLiveData<School>()
    val school: LiveData<School> = _school

    fun setSchool(school: School) {
        _school.value = school
    }
}
