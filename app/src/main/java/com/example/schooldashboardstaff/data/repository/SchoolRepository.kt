package com.example.schooldashboardstaff.data.repository

import android.util.Log
import com.example.schooldashboardstaff.data.firebase.AuthManager
import com.example.schooldashboardstaff.data.firebase.SchoolManager
import com.example.schooldashboardstaff.data.firebase.UserManager
import com.example.schooldashboardstaff.data.model.School
import com.example.schooldashboardstaff.data.model.User
import com.example.schooldashboardstaff.data.model.UserRole

class SchoolRepository {

    private val authManager: AuthManager = AuthManager()
    private val userManager: UserManager = UserManager()
    private val schoolManager: SchoolManager = SchoolManager()
    private val authRepository: AuthRepository = AuthRepository()





    fun createSchoolWithAdmin(
        school: School,
        adminEmail: String,
        adminPassword: String,
        adminFirstName: String,
        adminLastName: String,
        adminUsername: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val CREATE_SCHOOL_FLOW_TAG = "CreateSchoolFlow"

        val superAdminUid = authManager.getCurrentUserId()

        if (superAdminUid == null) {
            onFailure(Exception("Super admin not logged in"))
            return
        }

        Log.d(CREATE_SCHOOL_FLOW_TAG, "Starting admin creation with email: $adminEmail")

        authManager.createUserWithEmailAndPassword(
            email = adminEmail,
            password = adminPassword,
            onSuccess = { uid ->
                Log.d(CREATE_SCHOOL_FLOW_TAG, "Admin account created with UID: $uid")

                val newUser = User(
                    uid = uid,
                    firstname = adminFirstName,
                    lastname = adminLastName,
                    username = adminUsername,
                    email = adminEmail,
                    role = UserRole.SCHOOL_ADMIN,
                    schoolId = ""
                )

                userManager.createUser(
                    newUser,
                    onSuccess = {
                        Log.d(CREATE_SCHOOL_FLOW_TAG, "User document created in Firestore for UID: $uid")

                        userManager.addUsernameMapping(
                            username = adminUsername,
                            email = adminEmail,
                            onSuccess = {
                                Log.d(CREATE_SCHOOL_FLOW_TAG, "Username mapping added for $adminUsername → $adminEmail")

                                val schoolToCreate = school.copy(
                                    adminIds = mutableListOf(uid),
                                    createdBy = superAdminUid
                                )

                                schoolManager.createSchool(
                                    schoolToCreate,
                                    onSuccess = { schoolId ->
                                        Log.d(CREATE_SCHOOL_FLOW_TAG, "School created with ID: $schoolId")

                                        userManager.updateUserSchoolId(
                                            uid = uid,
                                            schoolId = schoolId,
                                            onSuccess = {
                                                Log.d(CREATE_SCHOOL_FLOW_TAG, "Updated admin user with schoolId: $schoolId")
                                                onSuccess()
                                            },
                                            onFailure = { exception ->
                                                Log.e(CREATE_SCHOOL_FLOW_TAG, "Failed to update admin user with schoolId", exception)

                                                // 🔁 Use new rollback logic
                                                deleteSchoolAndAdmins(
                                                    school = schoolToCreate.copy(id = schoolId),
                                                    onSuccess = {
                                                        Log.d(CREATE_SCHOOL_FLOW_TAG, "Rollback successful: school + user + auth deleted")
                                                        onFailure(Exception("School created but failed to link admin. Rolled back."))
                                                    },
                                                    onFailure = {
                                                        Log.e(CREATE_SCHOOL_FLOW_TAG, "Rollback failed during school + admin deletion", it)
                                                        onFailure(Exception("School created but failed to link admin. Manual cleanup may be required."))
                                                    }
                                                )
                                            }
                                        )
                                    },
                                    onFailure = { exception ->
                                        Log.e(CREATE_SCHOOL_FLOW_TAG, "Failed to create school, rolling back user and username", exception)
                                        authRepository.deleteUserCompletely(
                                            uid = uid,
                                            username = adminUsername,
                                            onSuccess = {
                                                Log.d(CREATE_SCHOOL_FLOW_TAG, "Rollback successful: user + auth deleted")
                                                onFailure(exception)
                                            },
                                            onFailure = {
                                                Log.e(CREATE_SCHOOL_FLOW_TAG, "Rollback failed for user + auth", it)
                                                onFailure(exception)
                                            }
                                        )
                                    }
                                )
                            },
                            onFailure = { exception ->
                                Log.e(CREATE_SCHOOL_FLOW_TAG, "Failed to add username mapping, rolling back user", exception)
                            }
                        )
                    },
                    onFailure = { exception ->
                        Log.e(CREATE_SCHOOL_FLOW_TAG, "Failed to create user document in Firestore", exception)
                        onFailure(exception)
                    }
                )
            },
            onFailure = { exception ->
                Log.e(CREATE_SCHOOL_FLOW_TAG, "Failed to create Firebase Auth user", exception)
                onFailure(exception)
            }
        )
    }



    fun assignAdminToSchool(
        schoolId: String,
        adminEmail: String,
        adminPassword: String,
        adminFirstName: String,
        adminLastName: String,
        adminUsername: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val TAG = "AssignAdminFlow"

        authManager.createUserWithEmailAndPassword(
            email = adminEmail,
            password = adminPassword,
            onSuccess = { uid ->
                Log.d(TAG, "Firebase Auth user created: $uid")

                val user = User(
                    uid = uid,
                    firstname = adminFirstName,
                    lastname = adminLastName,
                    username = adminUsername,
                    email = adminEmail,
                    role = UserRole.SCHOOL_ADMIN,
                    schoolId = schoolId
                )

                userManager.createUser(
                    user,
                    onSuccess = {
                        Log.d(TAG, "User document created in Firestore")

                        userManager.addUsernameMapping(
                            username = adminUsername,
                            email = adminEmail,
                            onSuccess = {
                                Log.d(TAG, "Username mapping created")

                                schoolManager.assignAdminToSchool(
                                    schoolId = schoolId,
                                    adminUid = uid,
                                    onSuccess = {
                                        Log.d(TAG, "Admin assigned to school successfully")
                                        onSuccess()
                                    },
                                    onFailure = { exception ->
                                        Log.e(TAG, "Failed to assign admin to school", exception)
                                        authRepository.deleteUserCompletely(
                                            uid = uid,
                                            username = adminUsername,
                                            onSuccess = {
                                                Log.d(TAG, "Rollback successful")
                                                onFailure(Exception("Failed to assign admin to school. Rolled back."))
                                            },
                                            onFailure = {
                                                Log.e(TAG, "Rollback failed", it)
                                                onFailure(Exception("Partial failure. Manual cleanup may be required."))
                                            }
                                        )
                                    }
                                )
                            },
                            onFailure = { exception ->
                                Log.e(TAG, "Failed to add username mapping", exception)
                            }
                        )
                    },
                    onFailure = { exception ->
                        Log.e(TAG, "Failed to create user document", exception)
                        onFailure(exception)
                    }
                )
            },
            onFailure = { exception ->
                Log.e(TAG, "Failed to create Firebase Auth user", exception)
                onFailure(exception)
            }
        )
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
        schoolManager.updateSchoolDetails(
            schoolId = schoolId,
            name = name,
            location = location,
            startingGrade = startingGrade,
            finalGrade = finalGrade,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }





    // SchoolRepository.kt
    fun checkUsernameAvailability(
        username: String,
        onResult: (Boolean) -> Unit
    ) {
        if (username.isBlank()) {
            onResult(false)
            return
        }

        userManager.getEmailByUsername(
            username = username,
            onSuccess = {
                onResult(false) // username taken
            },
            onFailure = {
                onResult(true) // username not found
            }
        )
    }



    fun listenToSchoolsWithAdminUsernames(
        onSuccess: (List<School>, Map<String, String>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        schoolManager.listenToSchools(
            onChange = { schools ->
                if (schools.isEmpty()) {
                    onSuccess(emptyList(), emptyMap())
                    return@listenToSchools
                }

                val adminIds = schools.flatMap { it.adminIds }.toSet()
                val usernameMap = mutableMapOf<String, String>()
                var completed = 0

                for (adminId in adminIds) {
                    userManager.getUserByUid(
                        uid = adminId,
                        onSuccess = { user ->
                            usernameMap[adminId] = user.username
                            completed++
                            if (completed == adminIds.size) {
                                onSuccess(schools, usernameMap)
                            }
                        },
                        onFailure = {
                            completed++
                            if (completed == adminIds.size) {
                                onSuccess(schools, usernameMap) // even if some fail
                            }
                        }
                    )
                }
            },
            onError = onFailure
        )
    }


    fun removeSchoolsListener() {
        schoolManager.removeSchoolsListener()
    }


    fun deleteSchoolAndAdmins(
        school: School,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val schoolId = school.id
        if (schoolId == null) {
            onFailure(Exception("School ID is null"))
            return
        }

        schoolManager.deleteSchoolById(
            schoolId,
            onSuccess = {
                if (school.adminIds.isEmpty()) {
                    onSuccess()
                } else {
                    deleteAdminsRecursive(school.adminIds, 0, onSuccess, onFailure)
                }
            },
            onFailure = onFailure
        )
    }

    private fun deleteAdminsRecursive(
        adminIds: List<String>,
        index: Int,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (index >= adminIds.size) {
            onSuccess()
            return
        }

        val uid = adminIds[index]

        userManager.getUserByUid(
            uid,
            onSuccess = { user ->
                if (user == null) {
                    Log.e("SchoolRepo", "User with ID $uid not found")
                    deleteAdminsRecursive(adminIds, index + 1, onSuccess, onFailure)
                    return@getUserByUid
                }

                authRepository.deleteUserCompletely(
                    uid,
                    user.username,
                    onSuccess = {
                        deleteAdminsRecursive(adminIds, index + 1, onSuccess, onFailure)
                    },
                    onFailure = onFailure
                )
            },
            onFailure = onFailure
        )
    }














//    fun getAllSchoolsWithAdminUsernames(
//        onSuccess: (List<School>, Map<String, String>) -> Unit,
//        onFailure: (Exception) -> Unit
//    ) {
//        schoolManager.getAllSchools(
//            onSuccess = { schools ->
//                if (schools.isEmpty()) {
//                    onSuccess(emptyList(), emptyMap())
//                    return@getAllSchools
//                }
//
//                val adminIds = schools.mapNotNull { it.adminIds.firstOrNull() }.toSet()
//                val usernameMap = mutableMapOf<String, String>()
//                var completed = 0
//                var failed = false
//
//                adminIds.forEach { adminId ->
//                    userManager.getUserByUid(
//                        uid = adminId,
//                        onSuccess = { user ->
//                            usernameMap[adminId] = user.username
//                            completed++
//                            if (completed == adminIds.size && !failed) {
//                                onSuccess(schools, usernameMap)
//                            }
//                        },
//                        onFailure = {
//                            completed++
//                            if (!failed) {
//                                failed = true
//                                onFailure(it)
//                            }
//                        }
//                    )
//                }
//            },
//            onFailure = { onFailure(it) }
//        )
//    }









}