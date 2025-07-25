package com.example.schooldashboardstaff.data.model.timetable

class TimetableSwapHandler(private val finalTimetable: FinalTimetable) {

    /**
     * Checks which periods are swappable with the selected one
     */
    fun computeSwappablePeriods(
        selectedClassId: String,
        selectedDay: Int,
        selectedPeriod: Int
    ): Array<Array<Boolean?>> {
        val currentClassGrid = finalTimetable.classSchedules[selectedClassId] ?: return Array(5) { Array(8) { null } }
        val selected = currentClassGrid[selectedDay][selectedPeriod]

        val result = Array(5) { row ->
            Array(8) { col ->
                if (row == selectedDay && col == selectedPeriod) null // skip itself
                else isSwappable(selectedClassId, selectedDay, selectedPeriod, row, col)
            }
        }

        return result
    }

    /**
     * Core logic to determine if two periods can be swapped
     */
    private fun isSwappable(
        classId: String,
        day1: Int,
        period1: Int,
        day2: Int,
        period2: Int
    ): Boolean {
        val grid = finalTimetable.classSchedules[classId] ?: return false
        val p1 = grid[day1][period1]
        val p2 = grid[day2][period2]

        // Self-swap is invalid
        if (day1 == day2 && period1 == period2) return false

        // If both periods are null, nothing to swap
        if (p1 == null && p2 == null) return false

        // Same teacher = swappable
        if (p1 != null && p2 != null && p1.teacherId == p2.teacherId) return true

        val t1Free = p1 == null || isTeacherFree(p1.teacherId, day2, period2)
        val t2Free = p2 == null || isTeacherFree(p2.teacherId, day1, period1)

        return t1Free && t2Free
    }

    /**
     * Checks if a teacher is free at a given day/period
     */
    private fun isTeacherFree(teacherId: String, day: Int, period: Int): Boolean {
        val grid = finalTimetable.teacherSchedules[teacherId] ?: return true
        return grid[day][period] == null
    }

    fun performSwap(classId: String, day1: Int, period1: Int, day2: Int, period2: Int) {
        val classGrid = finalTimetable.classSchedules[classId] ?: return
        val teacher1 = classGrid[day1][period1]?.teacherId
        val teacher2 = classGrid[day2][period2]?.teacherId

        // Swap in class timetable
        val temp = classGrid[day1][period1]
        classGrid[day1][period1] = classGrid[day2][period2]
        classGrid[day2][period2] = temp

        // Swap in teacher timetable if applicable
        if (teacher1 != null) {
            val t1Grid = finalTimetable.teacherSchedules[teacher1]
            t1Grid?.let {
                val tempPeriod = it[day1][period1]
                it[day1][period1] = it[day2][period2]
                it[day2][period2] = tempPeriod
            }
        }

        if (teacher2 != null && teacher2 != teacher1) {
            val t2Grid = finalTimetable.teacherSchedules[teacher2]
            t2Grid?.let {
                val tempPeriod = it[day1][period1]
                it[day1][period1] = it[day2][period2]
                it[day2][period2] = tempPeriod
            }
        }
    }

    fun getFinalTimetable(): FinalTimetable = finalTimetable


}
