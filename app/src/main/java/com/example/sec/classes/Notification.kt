package com.example.sec.classes

import java.sql.Timestamp

data class Notification(
    val id: Int,
    val employeeId: Int?,
    val clientId: Int?,
    val title: String,
    val message: String,
    val sentAt: Timestamp,
    val isRead: Boolean
)