package com.example.sec.classes

import java.sql.Timestamp

data class Client(
    val id: Int,
    val lastName: String,
    val firstName: String,
    val patronymic: String?,
    val phone: String,
    val email: String?,
    val address: String?,
    val passwordHash: String?,
    val createdAt: Timestamp
)