package com.example.sec.classes

import java.sql.Date
import java.sql.Timestamp

data class Report(
    val id: Int,
    val reportType: String,
    val periodStart: Date,
    val periodEnd: Date,
    val generatedBy: Int,
    val filePath: String?,
    val createdAt: Timestamp
)