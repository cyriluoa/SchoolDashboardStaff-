package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.classes



import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schooldashboardstaff.data.model.School
import com.example.schooldashboardstaff.data.model.SchoolClass
import com.example.schooldashboardstaff.data.model.input.MissingGradeInput
import com.example.schooldashboardstaff.data.repository.SchoolClassRepository
import com.google.firebase.firestore.ListenerRegistration

class SchoolClassesViewModel(private val school: School) : ViewModel() {

    private val repository = SchoolClassRepository(school.id)

    private val _classes = MutableLiveData<List<SchoolClass>>()
    val classes: LiveData<List<SchoolClass>> get() = _classes

    private val _missingGrades = MutableLiveData<List<Int>>()
    val missingGrades: LiveData<List<Int>> get() = _missingGrades

    private var listenerRegistration: ListenerRegistration? = null

    init {
        startListeningToClasses()
    }

    private fun startListeningToClasses() {
        listenerRegistration = repository.listenToAllClasses(
            onDataChanged = { updatedList ->
                val sorted = updatedList.sortedWith(compareBy({ it.grade }, { it.section }))
                _classes.value = sorted

                // Compute missing grades
                val existingGrades = sorted.map { it.grade }.toSet()
                val expectedGrades = (school.startingGrade..school.finalGrade).toSet()
                val missing = expectedGrades - existingGrades
                _missingGrades.value = missing.toList().sorted()
            },
            onError = {
                _classes.value = emptyList()
                _missingGrades.value = emptyList()
            }
        )
    }


    fun populateMissingGrades(inputs: List<MissingGradeInput>) {
        val newClasses = mutableListOf<SchoolClass>()

        for (gradeInput in inputs) {
            for (section in gradeInput.sectionInputs) {
                val sectionChar = section.sectionName
                val maxStudents = section.maxStudents.toIntOrNull() ?: continue
                val maxPeriods = section.maxPeriods.toIntOrNull() ?: continue

                val schoolClass = SchoolClass(
                    schoolId = school.id, // from ViewModel constructor
                    grade = gradeInput.grade,
                    section = sectionChar,
                    maxStudents = maxStudents,
                    maxPeriods = maxPeriods,
                    periodsLeft = maxPeriods
                )
                newClasses.add(schoolClass)
            }
        }

        repository.addMultipleClasses(
            newClasses,
            onSuccess = { /* Optional: update UI or toast */ },
            onFailure = { e -> Log.e("PopulateGrades", "Error: ${e.localizedMessage}") }
        )
    }


    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}

