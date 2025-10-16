package com.example.sec.classes

import java.math.BigDecimal
import java.sql.Date
import java.sql.Timestamp

data class Contract(
    val id: Int,
    val clientId: Int,
    val serviceId: Int,
    val startDate: Date,
    val endDate: Date,
    val totalAmount: BigDecimal?,
    val status: String,
    val createdAt: Timestamp
)