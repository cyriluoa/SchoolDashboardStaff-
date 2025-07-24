package com.example.schooldashboardstaff.data.firebase

import com.example.schooldashboardstaff.data.model.timetable.TimetableDocument
import com.example.schooldashboardstaff.utils.Constants
import jakarta.inject.Inject

class TimetableManager @Inject constructor() : FirestoreManager() {

    fun saveTimetable(schoolId: String, document: TimetableDocument, onResult: (Boolean) -> Unit) {
        val id = db.collection(Constants.SCHOOLS_COLLECTION).document(schoolId)
            .collection(Constants.TIMETABLES_COLLECTION).document().id

        val docWithId = document.copy(id = id)

        db.collection(Constants.SCHOOLS_COLLECTION).document(schoolId)
            .collection(Constants.TIMETABLES_COLLECTION)
            .document(id)
            .set(docWithId)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
}
