package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.timetable

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schooldashboardstaff.data.model.SchoolClass
import com.example.schooldashboardstaff.data.model.SchoolClassState
import com.example.schooldashboardstaff.data.model.Subject
import com.example.schooldashboardstaff.data.model.User
import com.example.schooldashboardstaff.data.model.timetable.FinalTimetable
import com.example.schooldashboardstaff.data.model.timetable.TimetableGenerator
import com.example.schooldashboardstaff.data.repository.FetchRepository
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@HiltViewModel
class TimetableViewModel @Inject constructor(
    private val fetchRepository: FetchRepository
) : ViewModel() {

    private val _canGenerateTimetable = MutableLiveData<Boolean>()
    val canGenerateTimetable: LiveData<Boolean> = _canGenerateTimetable

    private var cachedSchoolClasses: List<SchoolClass> = emptyList()
    private var listenerRegistration: ListenerRegistration? = null




    fun observeSchoolClasses(schoolId: String) {
        listenerRegistration?.remove()

        listenerRegistration = fetchRepository.listenToSchoolClasses(
            schoolId = schoolId,
            onSuccess = { classList ->
                cachedSchoolClasses = classList
                val allStableOrFull = classList.all { schoolClass ->
                    when (schoolClass.state) {
                        SchoolClassState.UNSTABLE_CLASS_TEACHER_ASSIGNED,
                        SchoolClassState.STABLE_STUDENTS_PRESENT,
                        SchoolClassState.STABLE_CLASS_FULL -> true
                        else -> false
                    }
                }
                _canGenerateTimetable.postValue(allStableOrFull)
            },
            onFailure = {
                _canGenerateTimetable.postValue(false)
            }
        )
    }

    fun generateTimetable(
        sharedTimetableViewModel: SharedTimetableViewModel,
        onComplete: (Boolean) -> Unit
    )
    {
        viewModelScope.launch {
            try {
                // 🔹 Step 1: Gather subject and teacher IDs
                val subjectIds = cachedSchoolClasses.flatMap { it.subjectAssignments.keys }.toSet().toList()
                val teacherIds = cachedSchoolClasses.flatMap { it.subjectAssignments.values }.toSet().toList()


                // 🔹 Step 2: Fetch Subjects
                val fetchedSubjects = suspendCancellableCoroutine<List<Subject>> { cont ->
                    fetchRepository.getSubjectsByIds(
                        schoolId = cachedSchoolClasses.firstOrNull()?.schoolId
                            ?: return@suspendCancellableCoroutine cont.resumeWith(Result.failure(Exception("Missing schoolId"))),
                        subjectIds = subjectIds,
                        onSuccess = { cont.resume(it) },
                        onFailure = {
                            Log.d("fetchedSubjects","Failure")
                            cont.resumeWith(Result.failure(it))
                        }
                    )
                }



                // 🔹 Step 3: Fetch Teachers
                val fetchedTeachers = suspendCancellableCoroutine<List<User>> { cont ->
                    fetchRepository.getUsersByIds(
                        userIds = teacherIds,
                        onSuccess = { cont.resume(it) },
                        onFailure = {
                            Log.d("fetchedTeachers","Failure")
                            cont.resumeWith(Result.failure(it)) }
                    )
                }

                Log.d("TimetableVM", "Classes: ${cachedSchoolClasses.size}, Subjects: ${subjectIds.size}, Teachers: ${teacherIds.size}")

                // 🔹 Step 4: Prepare input and build placement plans
                val generator = TimetableGenerator(
                    schoolClasses = cachedSchoolClasses,
                    subjects = fetchedSubjects,
                    teachers = fetchedTeachers
                )

                val input = generator.prepareInput()
                val placementPlans = generator.generatePlacementPlans(input)

//                Log.d("TimetableInput", input.toString())
//                placementPlans
//                    .groupBy { it.classId }
//                    .forEach { (classId, plans) ->
//                        Log.d("BalancedPlacementPlans", "Class ID: $classId")
//                        plans.forEachIndexed { index, plan ->
//                            val row = plan.perDayDistribution.joinToString(", ")
//                            Log.d("BalancedPlacementPlans", "  Subject #$index → [$row]")
//                        }
//                    }
//
//                Log.d("BalancedPlacementPlans", placementPlans.toString())

                val finalTimetable: FinalTimetable = generator.assignTimetable(placementPlans)


                sharedTimetableViewModel.setFinalTimetable(finalTimetable) // <-- store it in shared VM
//                Log.d("FinalTimetable", finalTimetable.classSchedules.toString())
//                Log.d("FinalTimetable", finalTimetable.teacherSchedules.toString())
//                for ((classId, grid) in finalTimetable.classSchedules) {
//                    Log.d("Timetable", "🧑‍🏫 Class ID: $classId")
//                    for (day in 0 until grid.size) {
//                        val row = grid[day].joinToString(" | ") { period ->
//                            period?.subjectName?.take(8)?.padEnd(8) ?: "Free    "
//                        }
//                        Log.d("Timetable", "  Day ${day + 1}: $row")
//                    }
//                }
//
//                for ((teacherId, grid) in finalTimetable.teacherSchedules) {
//                    Log.d("Timetable", "👩‍🏫 Teacher ID: $teacherId")
//                    for (day in 0 until grid.size) {
//                        val row = grid[day].joinToString(" | ") { period ->
//                            period?.subjectName?.take(8)?.padEnd(8) ?: "Free    "
//                        }
//                        Log.d("Timetable", "  Day ${day + 1}: $row")
//                    }
//                }



                onComplete(true)

            } catch (e: Exception) {
                e.printStackTrace()
                onComplete(false)
            }
        }
    }




    override fun onCleared() {
        super.onCleared()
        listenerRegistration?.remove()
    }
}

