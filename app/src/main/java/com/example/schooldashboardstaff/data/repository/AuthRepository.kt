package com.example.schooldashboardstaff.data.repository

import android.util.Log
import com.example.schooldashboardstaff.data.firebase.AuthManager
import com.example.schooldashboardstaff.data.firebase.UserManager
import com.example.schooldashboardstaff.data.model.User
import com.google.firebase.Firebase
import com.google.firebase.functions.functions

class AuthRepository(
    private val authManager: AuthManager = AuthManager(),
    private val userManager: UserManager = UserManager()
) {

    fun loginWithUsername(
        username: String,
        password: String,
        onSuccess: (User) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        userManager.getEmailByUsername(
            username = username,
            onSuccess = { email ->
                authManager.signInWithEmailAndPassword(
                    email = email,
                    password = password,
                    onSuccess = {
                        val uid = authManager.getCurrentUserId()
                        if (uid != null) {
                            userManager.getUserByUid(
                                uid,
                                onSuccess = { user -> onSuccess(user) },
                                onFailure = onFailure
                            )
                        } else {
                            onFailure(Exception("UID not found after login"))
                        }
                    },
                    onFailure = onFailure
                )
            },
            onFailure = onFailure
        )
    }


    fun deleteUserCompletely(uid: String, username: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        Log.d("AuthRepository", "deleteUserCompletely() called with uid=$uid and username=$username")
        userManager.deleteUserById(uid,
            onSuccess = {
                userManager.deleteUsernameMapping(username,
                    onSuccess = {
                        deleteUserFromAuth(uid, onSuccess, onFailure)
                    },
                    onFailure = onFailure
                )
            },
            onFailure = onFailure
        )
    }

    private fun deleteUserFromAuth(uid: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val data = mapOf("uid" to uid as Any)
        Log.d("AuthRepository", "Calling cloud function with data: $data")

        Firebase.functions
            .getHttpsCallable("deleteUserByUid")
            .call(data)
            .addOnSuccessListener {
                Log.d("AuthRepository", "Successfully deleted auth user with uid: $uid")
                onSuccess()
            }
            .addOnFailureListener {
                Log.e("AuthRepository", "Failed to delete auth user with uid: $uid", it)
                onFailure(it)
            }
    }




    fun signOut() {
        authManager.signOut()
    }

    fun isUserLoggedIn(): Boolean {
        return authManager.isLoggedIn()
    }

    fun getCurrentUserUid(): String? {
        return authManager.getCurrentUserId()
    }
}
