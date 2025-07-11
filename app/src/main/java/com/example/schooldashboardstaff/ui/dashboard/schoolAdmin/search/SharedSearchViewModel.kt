package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schooldashboardstaff.data.model.Subject
import com.example.schooldashboardstaff.data.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class SharedSearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : ViewModel() {

    private val _subjects = MutableLiveData<List<Subject>>()
    val subjects: LiveData<List<Subject>> get() = _subjects

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _query = MutableLiveData<String>("")
    val query: LiveData<String> get() = _query

    private var currentSchoolId: String? = null
    private var currentGrade: Int? = null

    private var assignedSubjectIds: Set<String> = emptySet()




    fun initSearch(schoolId: String, grade: Int, assignedIds: Set<String>) {
        currentSchoolId = schoolId
        currentGrade = grade
        assignedSubjectIds = assignedIds
        fetchSubjects() // initial load
    }

    fun updateQuery(query: String) {
        _query.value = query
        fetchSubjects()
    }

    private fun fetchSubjects() {
        val schoolId = currentSchoolId ?: return
        val grade = currentGrade ?: return
        val queryText = _query.value ?: ""

        viewModelScope.launch {
            _loading.value = true
            try {
                val unassignedSubjects = searchRepository.getUnassignedSubjectsForGrade(
                    schoolId = schoolId,
                    grade = grade,
                    assignedSubjectIds = assignedSubjectIds
                )

                _subjects.value = if (queryText.isBlank()) {
                    unassignedSubjects
                } else {
                    unassignedSubjects.filter {
                        it.name.contains(queryText, ignoreCase = true)
                    }
                }
            } catch (e: Exception) {
                Log.e("SharedSearchVM", "Error fetching subjects", e)
                _subjects.value = emptyList()
            } finally {
                _loading.value = false
            }
        }
    }

}

