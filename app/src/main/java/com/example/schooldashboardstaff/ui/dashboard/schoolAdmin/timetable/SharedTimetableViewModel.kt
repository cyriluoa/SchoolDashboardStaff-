package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.timetable

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schooldashboardstaff.data.model.timetable.FinalTimetable
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class SharedTimetableViewModel @Inject constructor() : ViewModel() {

    private val _finalTimetable = MutableLiveData<FinalTimetable>()
    val finalTimetable: LiveData<FinalTimetable> = _finalTimetable

    fun setFinalTimetable(timetable: FinalTimetable) {
        _finalTimetable.value = timetable
    }

    fun clear() {
        _finalTimetable.value = null
    }
}
