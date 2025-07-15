package com.example.schooldashboardstaff.data.repository


import com.example.schooldashboardstaff.data.firebase.AuthManager
import com.example.schooldashboardstaff.data.firebase.TeacherManager
import com.example.schooldashboardstaff.data.model.User
import com.google.firebase.firestore.ListenerRegistration
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TeacherRepository @Inject constructor(
    private val authManager: AuthManager,
    private val teacherManager: TeacherManager
) {

    fun createTeacherUser(
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

        val userWithCreatedBy = user.copy(createdBy = adminUid)

        teacherManager.createTeacherWithAuth(
            email = email,
            password = password,
            user = userWithCreatedBy,
            onSuccess = {
                // Now assign the teacher to each selected subject
                teacherManager.assignTeacherToSubjects(
                    teacherId = userWithCreatedBy.uid,
                    schoolId = userWithCreatedBy.schoolId,
                    subjectIds = userWithCreatedBy.subjectToClassMap?.keys ?: emptySet(),
                    onSuccess = {
                        onSuccess()
                    },
                    onFailure = { subjectAssignError ->
                        onFailure(subjectAssignError)
                    }
                )
            },
            onFailure = onFailure
        )
    }


    fun listenToTeachers(
        schoolId: String,
        onUpdate: (List<User>) -> Unit,
        onError: (String) -> Unit
    ): ListenerRegistration {
        return teacherManager.listenToTeachers(schoolId, onUpdate, onError)
    }

}
