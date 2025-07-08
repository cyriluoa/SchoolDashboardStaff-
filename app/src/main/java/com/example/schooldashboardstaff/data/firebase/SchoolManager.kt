package com.example.schooldashboardstaff.data.firebase

import android.util.Log
import com.example.schooldashboardstaff.data.model.School
import com.example.schooldashboardstaff.data.model.User
import com.example.schooldashboardstaff.utils.Constants
import com.google.firebase.firestore.ListenerRegistration

class SchoolManager : FirestoreManager(){


    val schoolsRef = db.collection(Constants.SCHOOLS_COLLECTION)

    private var schoolsListener: ListenerRegistration? = null


    fun createSchool(
        school: School,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val docRef = schoolsRef.document() // generate once
        val schoolWithId = school.copy(id = docRef.id)

        docRef.set(schoolWithId)
            .addOnSuccessListener { onSuccess(docRef.id) }
            .addOnFailureListener { onFailure(it) }
    }



    fun assignAdminToSchool(
        schoolId: String,
        adminUid: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        db.runTransaction { transaction ->
            val schoolRef = schoolsRef.document(schoolId)
            val snapshot = transaction.get(schoolRef)
            val currentAdminIds = snapshot.get("adminIds") as? List<String> ?: emptyList()

            if (adminUid in currentAdminIds) {
                // Already assigned, no need to update
                return@runTransaction null
            }

            val updatedAdminIds = currentAdminIds + adminUid
            transaction.update(schoolRef, "adminIds", updatedAdminIds)
            null
        }.addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener {
            onFailure(it)
        }
    }



    fun deleteSchoolById(
        schoolId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        schoolsRef.document(schoolId)
            .delete()
            .addOnSuccessListener {
                Log.d("SchoolManager", "Successfully deleted school: $schoolId")
                onSuccess()
            }
            .addOnFailureListener {
                Log.e("SchoolManager", "Failed to delete school: $schoolId", it)
                onFailure(it)
            }
    }




    fun listenToSchools(
        onChange: (List<School>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        schoolsListener?.remove() // remove existing listener if already set

        schoolsListener = schoolsRef.addSnapshotListener { snapshots, error ->
            if (error != null) {
                onError(error)
                return@addSnapshotListener
            }

            if (snapshots != null) {
                val schools = snapshots.documents.mapNotNull { it.toObject(School::class.java) }
                onChange(schools)
            }
        }
    }


    fun removeSchoolsListener() {
        schoolsListener?.remove()
        schoolsListener = null
    }

    fun updateSchoolDetails(
        schoolId: String,
        name: String,
        location: String,
        startingGrade: Int,
        finalGrade: Int,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val schoolRef = schoolsRef.document(schoolId)

        val updates = mapOf(
            "name" to name,
            "location" to location,
            "startingGrade" to startingGrade,
            "finalGrade" to finalGrade
        )

        schoolRef.update(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun getSchoolById(
        schoolId: String,
        onSuccess: (School) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        schoolsRef
            .document(schoolId)
            .get()
            .addOnSuccessListener { document ->
                val school = document.toObject(School::class.java)



                if (school != null) {
                    Log.d("SchoolManager",school.name)
                    onSuccess(school)
                } else {
                    onFailure(Exception("User is null"))
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }




//    fun getAllSchools(
//        onSuccess: (List<School>) -> Unit,
//        onFailure: (Exception) -> Unit
//    ) {
//        schoolsRef.get()
//            .addOnSuccessListener { querySnapshot ->
//                val schools = querySnapshot.documents.mapNotNull { it.toObject(School::class.java) }
//                onSuccess(schools)
//            }
//            .addOnFailureListener { exception ->
//                onFailure(exception)
//            }
//    }





}