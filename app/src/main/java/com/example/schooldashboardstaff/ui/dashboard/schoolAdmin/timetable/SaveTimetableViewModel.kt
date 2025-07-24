package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.timetable

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schooldashboardstaff.data.model.timetable.FinalTimetable
import com.example.schooldashboardstaff.data.model.timetable.TimetableDocument
import com.example.schooldashboardstaff.data.model.timetable.toSerializable
import com.example.schooldashboardstaff.data.repository.TimetableRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class SaveTimetableViewModel @Inject constructor(
    private val timetableRepository: TimetableRepository
) : ViewModel() {

    private val _saveStatus = MutableLiveData<Boolean?>()
    val saveStatus: LiveData<Boolean?> = _saveStatus

    fun saveDraftTimetable(schoolId: String, draftName: String, timetable: FinalTimetable) {
        val document = TimetableDocument(
            draftName = draftName,
            isActive = false,
            timetable = timetable.toSerializable()
        )



        timetableRepository.saveDraftTimetable(schoolId, document) { success ->
            _saveStatus.value = success
        }
    }
}
