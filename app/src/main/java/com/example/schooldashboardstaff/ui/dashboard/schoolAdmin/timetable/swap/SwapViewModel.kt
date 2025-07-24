package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.timetable.swap

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schooldashboardstaff.data.model.timetable.FinalTimetable
import com.example.schooldashboardstaff.data.model.timetable.Period
import com.example.schooldashboardstaff.data.model.timetable.TimetableGrid
import com.example.schooldashboardstaff.data.model.timetable.TimetableSwapHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class SwapViewModel @Inject constructor() : ViewModel() {

    private var timetableHandler: TimetableSwapHandler? = null

    private val _swappabilityGrid = MutableLiveData<Array<Array<Boolean?>>?>()
    val swappabilityGrid: LiveData<Array<Array<Boolean?>>?> = _swappabilityGrid

    fun setFinalTimetable(timetable: FinalTimetable) {
        timetableHandler = TimetableSwapHandler(timetable)
    }

    fun updateSelectedCell(classId: String, day: Int, period: Int) {
        _swappabilityGrid.value = timetableHandler?.computeSwappablePeriods(classId, day, period)
    }

    fun clearSelection() {
        _swappabilityGrid.value = null
    }


}

