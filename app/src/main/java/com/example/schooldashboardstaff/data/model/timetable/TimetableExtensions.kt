package com.example.schooldashboardstaff.data.model.timetable



fun FinalTimetable.toSerializable(): FinalSerializableTimetable {
    fun flattenGrid(grid: Array<Array<Period?>>): FlatTimetableGrid {
        val flat = mutableListOf<SerializablePeriodWrapper>()
        for (day in grid.indices) {
            for (period in grid[day].indices) {
                flat.add(SerializablePeriodWrapper(day, period, grid[day][period]))
            }
        }
        return flat
    }

    return FinalSerializableTimetable(
        classSchedules = this.classSchedules.mapValues { flattenGrid(it.value) },
        teacherSchedules = this.teacherSchedules.mapValues { flattenGrid(it.value) }
    )
}


