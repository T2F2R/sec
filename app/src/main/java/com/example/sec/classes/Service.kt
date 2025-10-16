package com.example.sec.classes

import java.math.BigDecimal

data class Service(
    val id: Int,
    val name: String,
    val description: String?,
    val price: BigDecimal
)