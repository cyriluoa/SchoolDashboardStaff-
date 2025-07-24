package com.example.schooldashboardstaff.data.model.timetable

data class SerializablePeriodWrapper(
    val day: Int,
    val period: Int,
    val periodData: Period? // nullable if it's a free period
)

typealias FlatTimetableGrid = List<SerializablePeriodWrapper>
