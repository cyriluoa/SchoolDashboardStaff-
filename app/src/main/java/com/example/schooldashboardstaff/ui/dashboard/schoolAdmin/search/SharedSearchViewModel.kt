package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schooldashboardstaff.data.model.Subject
import com.example.schooldashboardstaff.data.model.User
import com.example.schooldashboardstaff.data.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import kotlin.collections.filter

@HiltViewModel
class SharedSearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : ViewModel() {

    private val _subjects = MutableLiveData<List<Subject>>()
    val subjects: LiveData<List<Subject>> get() = _subjects

    private val _searchResults = MutableLiveData<List<User>>()
    val searchResults: LiveData<List<User>> get() = _searchResults

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _query = MutableLiveData<String>("")
    val query: LiveData<String> get() = _query



    private var currentSchoolId: String? = null
    private var currentGrade: Int? = null
    private var assignedSubjectIds: Set<String> = emptySet()

    private var currentSubject: Subject? = null


    // 🔴 CLASS-BASED SUBJECT SEARCH
    fun initSearchSubjectsForClass(schoolId: String, grade: Int, assignedIds: Set<String>) {
        currentSchoolId = schoolId
        currentGrade = grade
        assignedSubjectIds = assignedIds
        fetchSubjects()
    }

    // 🔵 TEACHER-BASED SUBJECT SEARCH (creation or edit)
    fun initSearchSubjectsForTeacher(schoolId: String, user: User?) {
        currentSchoolId = schoolId
        currentGrade = null // teacher mode
        assignedSubjectIds = user?.subjectToClassMap?.keys?.toSet() ?: emptySet()
        fetchSubjects()
    }

    // 🟢 INIT TEACHER SEARCH FOR ASSIGN
    fun initSearchTeachersForAssign(schoolId: String, subject: Subject) {
        currentSchoolId = schoolId
        currentSubject = subject
        searchTeachersToAssign(schoolId, subject)
    }


    fun updateQuery(query: String) {
        _query.value = query
        fetchSubjects()
    }

    private fun fetchSubjects() {
        val schoolId = currentSchoolId ?: return
        val queryText = _query.value ?: ""

        viewModelScope.launch {
            _loading.value = true
            try {
                val subjectsList = when {
                    currentGrade != null -> {
                        // CLASS MODE
                        searchRepository.getUnassignedSubjectsForGrade(
                            schoolId = schoolId,
                            grade = currentGrade!!,
                            assignedSubjectIds = assignedSubjectIds
                        )
                    }
                    assignedSubjectIds.isEmpty() -> {
                        // TEACHER CREATION (no subject assigned yet)
                        searchRepository.getAllSubjects(schoolId)
                    }
                    else -> {
                        // TEACHER EDIT MODE
                        searchRepository.getUnassignedSubjectsForTeacher(
                            schoolId = schoolId,
                            assignedSubjectIds = assignedSubjectIds
                        )
                    }
                }

                _subjects.value = if (queryText.isBlank()) {
                    subjectsList
                } else {
                    subjectsList.filter {
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

    private fun searchTeachersToAssign(schoolId: String, subject: Subject) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val teacherList = searchRepository.getTeachersForASubject(schoolId, subject)

                val filtered = teacherList.filter { teacher: User ->
                    val periodsLeft = (teacher.maxPeriods ?: 0) - (teacher.assignedPeriods ?: 0)
                    periodsLeft >= subject.periodCount
                }

                _searchResults.value = filtered
            } catch (e: Exception) {
                Log.e("SharedSearchVM", "Error fetching teachers", e)
                _searchResults.value = emptyList()
            } finally {
                _loading.value = false
            }
        }
    }
}


