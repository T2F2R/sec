package com.example.sec.classes

data class ScheduleItem(
    val id: Int,
    val date: String,
    val startTime: String,
    val endTime: String,
    val notes: String,
    val objectName: String,
    val objectAddress: String
)

data class ScheduleResponse(
    val success: Boolean,
    val schedule: List<ScheduleItem> = emptyList(),
    val error: String? = null
)