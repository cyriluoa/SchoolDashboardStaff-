package com.example.schooldashboardstaff.data.firebase



import android.util.Log


import com.example.schooldashboardstaff.data.model.User
import com.example.schooldashboardstaff.data.model.UserRole
import com.example.schooldashboardstaff.data.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TeacherManager @Inject constructor(
    private val authManager: AuthManager,
    private val authRepository: AuthRepository,
    private val userManager: UserManager
) : FirestoreManager() {

    private val TAG = "TeacherManager"

    fun createTeacherWithAuth(
        email: String,
        password: String,
        user: User,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val adminUid = authManager.getCurrentUserId()
        if (adminUid == null) {
            onFailure(Exception("Admin not logged in"))
            return
        }

        Log.d(TAG, "Creating teacher auth account for $email")

        authManager.createUserWithEmailAndPassword(
            email = email,
            password = password,
            onSuccess = { uid ->
                Log.d(TAG, "Teacher Auth created with UID: $uid")

                val teacherUser = user.copy(uid = uid, role = UserRole.TEACHER)

                userManager.createUser(
                    teacherUser,
                    onSuccess = {
                        Log.d(TAG, "Teacher Firestore document created for UID: $uid")

                        userManager.addUsernameMapping(
                            username = teacherUser.username,
                            email = teacherUser.email,
                            onSuccess = {
                                Log.d(TAG, "Username mapping created for ${teacherUser.username}")
                                onSuccess()
                            },
                            onFailure = { mappingError ->
                                Log.e(TAG, "Failed to add username mapping", mappingError)
                                rollbackTeacherCreation(teacherUser, mappingError, onFailure)
                            }
                        )
                    },
                    onFailure = { firestoreError ->
                        Log.e(TAG, "Failed to create teacher document", firestoreError)
                        rollbackTeacherCreation(user.copy(uid = uid), firestoreError, onFailure)
                    }
                )
            },
            onFailure = { authError ->
                Log.e(TAG, "Failed to create Auth user for teacher", authError)
                onFailure(authError)
            }
        )
    }

    private fun rollbackTeacherCreation(user: User, cause: Exception, onFailure: (Exception) -> Unit) {
        authRepository.deleteUserCompletely(
            uid = user.uid,
            username = user.username,
            onSuccess = {
                Log.d(TAG, "Rollback successful for user: ${user.uid}")
                onFailure(Exception("Rollback complete due to: ${cause.message}"))
            },
            onFailure = { rollbackError ->
                Log.e(TAG, "Rollback failed", rollbackError)
                onFailure(Exception("Failed to rollback: ${rollbackError.message}"))
            }
        )
    }
}
