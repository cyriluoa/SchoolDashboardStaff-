package com.example.schooldashboardstaff.data.firebase



import android.util.Log


import com.example.schooldashboardstaff.data.model.User
import com.example.schooldashboardstaff.data.model.UserRole
import com.example.schooldashboardstaff.data.repository.AuthRepository
import com.example.schooldashboardstaff.utils.Constants
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Source
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

                                // ✅ Assign subjects here
                                val subjectIds = teacherUser.subjectToClassMap?.keys ?: emptySet()
                                assignTeacherToSubjects(
                                    teacherId = uid,
                                    schoolId = teacherUser.schoolId,
                                    subjectIds = subjectIds,
                                    onSuccess = onSuccess,
                                    onFailure = onFailure
                                )
                            },
                            onFailure = { mappingError ->
                                Log.e(TAG, "Failed to add username mapping", mappingError)
                                rollbackTeacherCreation(teacherUser, mappingError, onFailure)
                            }
                        )
                    },
                    onFailure = { firestoreError ->
                        Log.e(TAG, "Failed to create teacher document", firestoreError)
                        rollbackTeacherCreation(teacherUser, firestoreError, onFailure)
                    }
                )
            },
            onFailure = { authError ->
                Log.e(TAG, "Failed to create Auth user for teacher", authError)
                onFailure(authError)
            }
        )
    }

    fun assignTeacherToSubjects(
        teacherId: String,
        schoolId: String,
        subjectIds: Set<String>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val batch = db.batch()

        subjectIds.forEach { subjectId ->
            val subjectRef = db.collection(Constants.SCHOOLS_COLLECTION)
                .document(schoolId)
                .collection(Constants.SUBJECTS_COLLECTION)
                .document(subjectId)
            Log.d("assignTeacherToSubjects", "SubjectId: $subjectId     TeacherId: $teacherId")

            batch.update(subjectRef, "teacherIds", FieldValue.arrayUnion(teacherId))
        }

        batch.commit()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
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

    fun listenToTeachers(
        schoolId: String,
        onUpdate: (List<User>) -> Unit,
        onError: (String) -> Unit
    ): ListenerRegistration {
        return db.collection("users")
            .whereEqualTo("schoolId", schoolId)
            .whereEqualTo("role", UserRole.TEACHER.name)
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->

                if (error != null) {
                    onError(error.message ?: "Unknown error occurred")
                    return@addSnapshotListener
                }

                if (snapshot == null) {
                    onUpdate(emptyList())
                    return@addSnapshotListener
                }

                // Ensure we're only acting on server-fresh data
                if (snapshot.metadata.isFromCache) {
                    Log.d("TeacherDebug", "Ignored cached snapshot")
                    return@addSnapshotListener
                }

                snapshot.documents.forEach { doc ->
                    Log.d("DeserializationDebug", "Document ID: ${doc.id}, Data: ${doc.data}")
                }

                val teachers = snapshot.toObjects(User::class.java)
                onUpdate(teachers)
            }
    }


}
