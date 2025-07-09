package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.subjects

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schooldashboardstaff.data.model.Subject
import com.example.schooldashboardstaff.data.repository.SubjectRepository
import com.google.firebase.firestore.ListenerRegistration

class SubjectsViewModel(private val schoolId: String) : ViewModel() {

    private val repository = SubjectRepository(schoolId)

    private val _subjects = MutableLiveData<List<Subject>>()
    val subjects: LiveData<List<Subject>> = _subjects

    private var listenerRegistration: ListenerRegistration? = null

    fun startListeningToSubjects() {
        listenerRegistration = repository.listenToSubjects(
            onSubjectsChanged = { subjectList ->
                _subjects.postValue(subjectList)
            },
            onError = { exception ->
                Log.e("SubjectViewModel", "Failed to load subjects", exception)
            }
        )
    }

    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}
