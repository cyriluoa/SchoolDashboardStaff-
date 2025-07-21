package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.timetable

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schooldashboardstaff.data.model.SchoolClass
import com.example.schooldashboardstaff.data.model.SchoolClassState
import com.example.schooldashboardstaff.data.repository.FetchRepository
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class TimetableViewModel @Inject constructor(
    private val fetchRepository: FetchRepository
) : ViewModel() {

    private val _canGenerateTimetable = MutableLiveData<Boolean>()
    val canGenerateTimetable: LiveData<Boolean> = _canGenerateTimetable

    private var listenerRegistration: ListenerRegistration? = null

    fun observeSchoolClasses(schoolId: String) {
        listenerRegistration?.remove() // clean up if already listening

        listenerRegistration = fetchRepository.listenToSchoolClasses(
            schoolId = schoolId,
            onSuccess = { classList ->
                val allStableOrFull = classList.all { schoolClass: SchoolClass ->
                    when (schoolClass.state) {
                        SchoolClassState.UNSTABLE_CLASS_TEACHER_ASSIGNED,
                        SchoolClassState.STABLE_STUDENTS_PRESENT,
                        SchoolClassState.STABLE_CLASS_FULL -> true
                        else -> false
                    }
                }
                _canGenerateTimetable.postValue(allStableOrFull)
            },
            onFailure = {
                _canGenerateTimetable.postValue(false)
            }
        )
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}
