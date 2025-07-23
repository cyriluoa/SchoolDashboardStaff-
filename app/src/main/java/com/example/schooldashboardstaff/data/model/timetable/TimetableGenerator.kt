package com.example.schooldashboardstaff.data.model.timetable

import android.util.Log
import com.example.schooldashboardstaff.data.model.SchoolClass
import com.example.schooldashboardstaff.data.model.Subject
import com.example.schooldashboardstaff.data.model.User
import com.example.schooldashboardstaff.data.model.UserRole
import kotlin.math.ceil

class TimetableGenerator(
    private val schoolClasses: List<SchoolClass>,
    private val subjects: List<Subject>,
    private val teachers: List<User>
) {

    fun prepareInput(): TimetableInput {
        val classInfoMap = schoolClasses.associateBy(
            keySelector = { it.id },
            valueTransform = {
                ClassInfo(
                    id = it.id,
                    subjectAssignments = it.subjectAssignments,
                    name = "${it.grade} - ${it.section}",
                    maxPeriods = it.maxPeriods
                )
            }
        )

        val subjectInfoMap = subjects.associateBy(
            keySelector = { it.id },
            valueTransform = {
                SubjectInfo(
                    id = it.id,
                    name = it.name,
                    periodCount = it.periodCount,
                    colorInt = it.colorInt
                )
            }
        )

        val teacherInfoMap = teachers
            .filter { it.role == UserRole.TEACHER }
            .associateBy(
                keySelector = { it.uid },
                valueTransform = {
                    val assignedPeriods = it.assignedPeriods ?: 0
                    val maxPerDay = ceil(assignedPeriods / 5.0).toInt() + 1
                    TeacherInfo(
                        id = it.uid,
                        username = it.username,
                        fullName = "${it.firstname} ${it.lastname}".trim(),
                        assignedPeriods = assignedPeriods,
                        maxPerDay = maxPerDay
                    )
                }
            )

        return TimetableInput(
            classInfoMap = classInfoMap,
            subjectInfoMap = subjectInfoMap,
            teacherInfoMap = teacherInfoMap
        )
    }

    fun generatePlacementPlans(input: TimetableInput): List<SubjectPlacementPlan> {
        val rawPlans = mutableListOf<SubjectPlacementPlan>()

        input.classInfoMap.forEach { (classId, classInfo) ->
            classInfo.subjectAssignments.forEach { (subjectId, teacherId) ->
                val classInfo = input.classInfoMap[classId] ?: return@forEach
                val subject = input.subjectInfoMap[subjectId] ?: return@forEach
                val teacher = input.teacherInfoMap[teacherId] ?: return@forEach
                val distribution = distributePeriodsOverDays(subject.periodCount)

                rawPlans.add(
                    SubjectPlacementPlan(
                        classId = classId,
                        className = classInfo.name,
                        subjectId = subjectId,
                        subjectName = subject.name,
                        teacherId = teacherId,
                        teacherUsername = teacher.username,
                        teacherFullName = teacher.fullName,
                        periodCount = subject.periodCount,
                        colorInt = subject.colorInt,
                        perDayDistribution = distribution
                    )
                )

            }
        }

        return buildBalancedClassPlans(rawPlans)
    }

    private fun distributePeriodsOverDays(total: Int, days: Int = 5): List<Int> {
        val base = total / days
        val remainder = total % days
        return List(days) { i -> if (i < remainder) base + 1 else base }
    }

    // 🔹 Step 2: Reorder perDayDistribution to make each column/day sum = 8
    private fun buildBalancedClassPlans(rawPlans: List<SubjectPlacementPlan>): List<SubjectPlacementPlan> {
        val MAX_ROW_DIFF = 2
        val days = 5
        val balancedPlans = mutableListOf<SubjectPlacementPlan>()
        val groupedByClass = rawPlans.groupBy { it.classId }

        for ((_, plans) in groupedByClass) {
            val subjectCount = plans.size
            val matrix = plans.map { it.perDayDistribution.toMutableList() }.toMutableList()

            val dayTotals = IntArray(days) { day -> matrix.sumOf { it[day] } }

            while (true) {
                var changed = false

                for (day in 0 until days) {
                    val total = dayTotals[day]
                    if (total == 8) continue

                    if (total > 8) {
                        // Reduce this day
                        for (i in 0 until subjectCount) {
                            for (targetDay in 0 until days) {
                                if (targetDay == day || dayTotals[targetDay] >= 8) continue

                                val transferable = minOf(matrix[i][day], 8 - dayTotals[targetDay])
                                if (transferable > 0) {
                                    val newRow = matrix[i].toMutableList().apply {
                                        this[day] -= transferable
                                        this[targetDay] += transferable
                                    }
                                    val diff = newRow.maxOrNull()!! - newRow.minOrNull()!!
                                    if (diff <= MAX_ROW_DIFF) {
                                        matrix[i][day] -= transferable
                                        matrix[i][targetDay] += transferable
                                        dayTotals[day] -= transferable
                                        dayTotals[targetDay] += transferable
                                        changed = true
                                    }
                                }
                            }
                        }
                    } else {
                        // Increase this day
                        for (i in 0 until subjectCount) {
                            for (sourceDay in 0 until days) {
                                if (sourceDay == day || dayTotals[sourceDay] <= 8) continue

                                val transferable = minOf(matrix[i][sourceDay], 8 - dayTotals[day])
                                if (transferable > 0) {
                                    val newRow = matrix[i].toMutableList().apply {
                                        this[sourceDay] -= transferable
                                        this[day] += transferable
                                    }
                                    val diff = newRow.maxOrNull()!! - newRow.minOrNull()!!
                                    if (diff <= MAX_ROW_DIFF) {
                                        matrix[i][sourceDay] -= transferable
                                        matrix[i][day] += transferable
                                        dayTotals[sourceDay] -= transferable
                                        dayTotals[day] += transferable
                                        changed = true
                                    }
                                }
                            }
                        }
                    }
                }

                if (!changed) break
            }

            // Optional: warning if day total isn't balanced due to row constraint


            if (dayTotals.any { it != 8 }) {
                throw IllegalStateException("Class plan could not be fully balanced: day total != 8 due to MAX_ROW_DIFF = $MAX_ROW_DIFF")
            }



            for ((index, plan) in plans.withIndex()) {
                balancedPlans.add(plan.copy(perDayDistribution = matrix[index].toList()))
            }
        }

        return balancedPlans
    }

    fun assignTimetable(plans: List<SubjectPlacementPlan>): FinalTimetable {
        val days = 5
        val periodsPerDay = 8
        val maxRetryTimePerClass = 20000L // 5 seconds in milliseconds

        val classSchedules = mutableMapOf<String, TimetableGrid>()
        val teacherSchedules = mutableMapOf<String, TimetableGrid>()

        val allClassIds = plans.map { it.classId }.toSet()
        val allTeacherIds = plans.map { it.teacherId }.toSet()

        for (classId in allClassIds) {
            classSchedules[classId] = Array(days) { arrayOfNulls(periodsPerDay) }
        }

        for (teacherId in allTeacherIds) {
            teacherSchedules[teacherId] = Array(days) { arrayOfNulls(periodsPerDay) }
        }

        val classGroupedPlans = plans.groupBy { it.classId }

        val overallStartTime = System.currentTimeMillis()

        for ((classId, subjectPlans) in classGroupedPlans) {
            val classStartTime = System.currentTimeMillis()
            var success = false
            var attempt = 0

            while (!success && System.currentTimeMillis() - classStartTime < maxRetryTimePerClass) {
                attempt++
                val shuffledPlans = subjectPlans.shuffled().sortedByDescending { it.periodCount }

                val classGrid = classSchedules[classId]!!
                val backupClassGrid = cloneGrid(classGrid)
                val backupTeacherGrids = teacherSchedules.mapValues { cloneGrid(it.value) }

                success = assignForClass(classId, shuffledPlans, classGrid, teacherSchedules)

                if (!success) {
                    restoreGrid(classGrid, backupClassGrid)
                    for ((tid, grid) in teacherSchedules) {
                        restoreGrid(grid, backupTeacherGrids[tid]!!)
                    }
                }
            }

            if (!success) {
                throw IllegalStateException("❌ Timetable assignment failed for class $classId after ${System.currentTimeMillis() - classStartTime} ms.")
            } else {
                Log.d("TimetableAssign", "✅ Class $classId assigned in $attempt attempt(s).")
            }
        }

        val overallEndTime = System.currentTimeMillis()
        Log.i("TimetableTiming", "🕒 Timetable generated in ${overallEndTime - overallStartTime} ms")

        return FinalTimetable(
            classSchedules = classSchedules,
            teacherSchedules = teacherSchedules
        )
    }



    private fun assignForClass(
        classId: String,
        plans: List<SubjectPlacementPlan>,
        classGrid: TimetableGrid,
        teacherGrids: Map<String, TimetableGrid>
    ): Boolean {
        // Calculates how many free periods a teacher has on each day
        fun teacherDailyFreeSlots(teacherGrid: TimetableGrid): List<Int> {
            return List(5) { day ->
                teacherGrid[day].count { it == null }
            }
        }

        val sortedPlans = plans.sortedWith(
            compareByDescending<SubjectPlacementPlan> { it.periodCount }
                .thenByDescending { plan ->
                    val teacherGrid = teacherGrids[plan.teacherId] ?: return@thenByDescending 0
                    val teacherDailyFree = teacherDailyFreeSlots(teacherGrid)

                    // Score based on how well the teacher can satisfy the plan’s distribution
                    plan.perDayDistribution.withIndex().sumOf { (day, needed) ->
                        minOf(needed, teacherDailyFree[day])
                    }
                }
        )

        return backtrackAssign(0, sortedPlans, classId, classGrid, teacherGrids)
    }



    private fun backtrackAssign(
        index: Int,
        plans: List<SubjectPlacementPlan>,
        classId: String,
        classGrid: TimetableGrid,
        teacherGrids: Map<String, TimetableGrid>
    ): Boolean {
        if (index == plans.size) return true

        val plan = plans[index]
        val teacherGrid = teacherGrids[plan.teacherId] ?: return false

        val classBackup = cloneGrid(classGrid)
        val teacherBackup = cloneGrid(teacherGrid)

        val candidateSlots = mutableListOf<Pair<Int, Int>>()
        for (day in 0 until 5) {
            val periodsNeeded = plan.perDayDistribution[day]
            val availableSlots = (0 until 8).filter {
                classGrid[day][it] == null && teacherGrid[day][it] == null
            }

            if (availableSlots.size < periodsNeeded) {
//                Log.e("backtrackAssign availableSlots.size < periodsNeeded","Failed here")
                return false
            }

            candidateSlots += availableSlots.shuffled().take(periodsNeeded).map { periodIdx ->
                day to periodIdx
            }
        }

        if (placeSubjectPeriods(plan, classId, classGrid, teacherGrid, candidateSlots)) {
            if (backtrackAssign(index + 1, plans, classId, classGrid, teacherGrids)) {
                return true
            }
        }

        restoreGrid(classGrid, classBackup)
        restoreGrid(teacherGrid, teacherBackup)
//        Log.e("backtrackAssign end of","Failed here")
        return false
    }

    private fun placeSubjectPeriods(
        plan: SubjectPlacementPlan,
        classId: String,
        classGrid: TimetableGrid,
        teacherGrid: TimetableGrid,
        slots: List<Pair<Int, Int>>
    ): Boolean {
        val period = Period(
            classId = classId,
            className = plan.className,
            subjectId = plan.subjectId,
            subjectName = plan.subjectName,
            colorInt = plan.colorInt,
            teacherId = plan.teacherId,
            teacherUsername = plan.teacherUsername,
            teacherFullName = plan.teacherFullName
        )

        var placed = 0
        for ((day, periodIdx) in slots) {
            if (classGrid[day][periodIdx] == null && teacherGrid[day][periodIdx] == null) {
                classGrid[day][periodIdx] = period
                teacherGrid[day][periodIdx] = period
                placed++
                if (placed == plan.periodCount) return true
            }
        }
//        Log.e("placeSubjectPeriods end of","Failed here")
        return false
    }

    private fun cloneGrid(grid: TimetableGrid): TimetableGrid {
        return Array(grid.size) { day -> grid[day].clone() }
    }

    private fun restoreGrid(target: TimetableGrid, backup: TimetableGrid) {
        for (day in 0 until target.size) {
            for (period in 0 until target[day].size) {
                target[day][period] = backup[day][period]
            }
        }
    }

}

