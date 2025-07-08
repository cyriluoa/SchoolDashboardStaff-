package com.example.schooldashboardstaff.data.firebase

import com.example.schooldashboardstaff.data.model.SchoolClass
import com.example.schooldashboardstaff.utils.Constants
import com.google.firebase.Firebase
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore

class SchoolClassManager(private val schoolId: String) : FirestoreManager() {

    companion object {
        private const val FIELD_GRADE = "grade"
    }

    private val classesRef = db.collection(Constants.SCHOOLS_COLLECTION)
        .document(schoolId)
        .collection(Constants.SCHOOL_CLASSES_COLLECTION)

    /**
     * Adds a new class to the current school.
     * If ID is empty, generates one automatically.
     */
    fun addClass(
        schoolClass: SchoolClass,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val docRef = if (schoolClass.id.isNotEmpty()) {
            classesRef.document(schoolClass.id)
        } else {
            classesRef.document()
        }

        val classWithId = schoolClass.copy(id = docRef.id)

        docRef.set(classWithId)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }


    fun addMultipleClasses(
        classes: List<SchoolClass>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val batch = Firebase.firestore.batch()

        classes.forEach { schoolClass ->
            val docRef = if (schoolClass.id.isNotEmpty()) {
                classesRef.document(schoolClass.id)
            } else {
                classesRef.document()
            }

            val classWithId = schoolClass.copy(id = docRef.id)
            batch.set(docRef, classWithId)
        }

        batch.commit()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }


    /**
     * Listens for all classes in this school, ordered by grade.
     */
    fun listenToClasses(
        onDataChanged: (List<SchoolClass>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration {
        return classesRef
            .orderBy(Constants.SCHOOL_CLASSES_FIELD_GRADE)
            .orderBy(Constants.SCHOOL_CLASSES_FIELD_SECTION) // 🔥 Added: secondary sort
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    onError(exception)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val classes = snapshot.documents.mapNotNull { it.toObject(SchoolClass::class.java) }
                    onDataChanged(classes)
                } else {
                    onDataChanged(emptyList())
                }
            }
    }


    /**
     * Deletes a specific class by ID.
     */
    fun deleteClass(
        classId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        classesRef.document(classId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    /**
     * Fetches a specific class by ID (one-time).
     */
    fun getClassById(
        classId: String,
        onSuccess: (SchoolClass?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        classesRef.document(classId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val schoolClass = document.toObject(SchoolClass::class.java)
                    onSuccess(schoolClass)
                } else {
                    onSuccess(null)
                }
            }
            .addOnFailureListener { onFailure(it) }
    }

    /**
     * Updates an existing class document.
     */
    fun updateClass(
        schoolClass: SchoolClass,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (schoolClass.id.isEmpty()) {
            onFailure(IllegalArgumentException("Class ID cannot be empty for update"))
            return
        }

        classesRef.document(schoolClass.id)
            .set(schoolClass)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun getClassesForGrade(
        grade: Int,
        onSuccess: (List<SchoolClass>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        classesRef
            .whereEqualTo(Constants.SCHOOL_CLASSES_FIELD_GRADE, grade)
            .get()
            .addOnSuccessListener { snapshot ->
                val classes = snapshot.documents.mapNotNull { it.toObject(SchoolClass::class.java) }
                onSuccess(classes)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }


}

