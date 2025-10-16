package com.example.sec.classes

import java.sql.Timestamp

data class DeviceToken(
    val id: Int,
    val employeeId: Int?,
    val clientId: Int?,
    val deviceToken: String,
    val platform: String,
    val createdAt: Timestamp
)