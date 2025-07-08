package com.example.schooldashboardstaff.data.repository


import com.example.schooldashboardstaff.data.firebase.SchoolClassManager
import com.example.schooldashboardstaff.data.model.SchoolClass
import com.google.firebase.firestore.ListenerRegistration


/**
 * Adds a new class to the school.
 */
class SchoolClassRepository(private val schoolId: String) {

    private val manager = SchoolClassManager(schoolId)

    fun addClass(
        schoolClass: SchoolClass,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        // Step 1: Fetch existing classes in the same grade
        manager.getClassesForGrade(
            grade = schoolClass.grade,
            onSuccess = { existingClasses ->
                // Step 2: Calculate next section
                val nextSection = calculateNextSection(existingClasses.map { it.section })

                // Step 3: Set the section and pass to manager
                val updatedClass = schoolClass.copy(section = nextSection)

                // Step 4: Add it
                manager.addClass(updatedClass, onSuccess, onFailure)
            },
            onFailure = { exception ->
                onFailure(exception)
            }
        )
    }

    fun addMultipleClasses(
        classes: List<SchoolClass>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        SchoolClassManager(schoolId).addMultipleClasses(classes, onSuccess, onFailure)
    }


    private fun calculateNextSection(existingSections: List<String>): String {
        val used = existingSections.toSet()
        for (char in 'A'..'Z') {
            val section = char.toString()
            if (section !in used) return section
        }
        return "Z+" // fallback if more than 26 sections somehow
    }

    fun listenToAllClasses(
        onDataChanged: (List<SchoolClass>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration {
        return manager.listenToClasses(onDataChanged, onError)
    }
}


