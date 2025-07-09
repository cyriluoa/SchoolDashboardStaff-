package com.example.schooldashboardstaff.data.firebase


import com.google.firebase.Firebase
import com.google.firebase.functions.functions
import javax.inject.Inject

class AuthManager @Inject constructor(): FirestoreManager() {

    fun signInWithEmailAndPassword(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ){
        auth.signInWithEmailAndPassword(email,password)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }


    fun createUserWithEmailAndPassword(
        email: String,
        password: String,
        onSuccess: (String) -> Unit, // Returns UID
        onFailure: (Exception) -> Unit
    ) {
        val data = mapOf(
            "email" to email as Any,
            "password" to password as Any
        )

        Firebase.functions
            .getHttpsCallable("createUserWithEmail")
            .call(data)
            .addOnSuccessListener { result ->
                val uid = (result.data as Map<*, *>)["uid"] as? String
                if (uid != null) {
                    onSuccess(uid)
                } else {
                    onFailure(Exception("UID missing from response"))
                }
            }
            .addOnFailureListener { onFailure(it) }
    }



    fun signOut(){
        auth.signOut()
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }


}