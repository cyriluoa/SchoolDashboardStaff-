package com.example.schooldashboardstaff.data.firebase

import android.util.Log
import com.example.schooldashboardstaff.data.model.User
import com.example.schooldashboardstaff.utils.Constants

class UserManager: FirestoreManager() {

    private val usersRef = db.collection(Constants.USERS_COLLECTION)
    private val usernamesRef = db.collection(Constants.USERNAMES_COLLECTION)

    fun getEmailByUsername(
        username: String,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        usernamesRef
            .document(username)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val email = document.getString(Constants.USERS_FIELD_EMAIL)
                    if (email != null) {
                        onSuccess(email)
                    } else {
                        onFailure(Exception("Email field missing"))
                    }
                } else {
                    onFailure(Exception("Username does not exist"))
                }
            }
            .addOnFailureListener(onFailure)
    }


    fun getUserByUid(
        uid: String,
        onSuccess: (User) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        usersRef
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                if (user != null) {
                    onSuccess(user)
                } else {
                    onFailure(Exception("User is null"))
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun createUser(
        user: User,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        usersRef
            .document(user.uid)
            .set(user)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun addUsernameMapping(
        username: String,
        email: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        usernamesRef
            .document(username)
            .set(mapOf("email" to email))
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }



    // Called when deleting school
    fun deleteUserById(uid: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        usersRef.document(uid)
            .delete()
            .addOnSuccessListener {
                Log.d("UserManager", "Successfully deleted user with uid: $uid")
                onSuccess()
            }
            .addOnFailureListener {
                Log.e("UserManager", "Failed to delete user with uid: $uid", it)
                onFailure(it)
            }
    }

    fun deleteUsernameMapping(username: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        usernamesRef.document(username)
            .delete()
            .addOnSuccessListener {
                Log.d("UserManager", "Successfully deleted username mapping: $username")
                onSuccess()
            }
            .addOnFailureListener {
                Log.e("UserManager", "Failed to delete username mapping: $username", it)
                onFailure(it)
            }
    }




    fun updateUserSchoolId(
        uid: String,
        schoolId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection(Constants.USERS_COLLECTION)
            .document(uid)
            .update(Constants.USERS_FIELD_SCHOOL_ID, schoolId)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }










}