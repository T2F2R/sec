package com.example.sec.classes

import com.google.gson.annotations.SerializedName

data class Employee(
    val id: Int,
    val lastName: String,
    val firstName: String,
    val patronymic: String?,
    val login: String?
)

data class LoginRequest(
    val login: String,
    val password: String
)

data class LoginResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("employee_id")
    val employeeId: Int? = null,

    @SerializedName("employee_name")
    val employeeName: String? = null,

    @SerializedName("error")
    val error: String? = null
)

data class RegisterRequest(
    val lastName: String,
    val firstName: String,
    val patronymic: String?,
    val passportSeries: Int,
    val passportNumber: Int,
    val login: String,
    val password: String
)

data class RegisterResponse(
    val success: Boolean,
    val employeeId: Int? = null,
    val employeeName: String? = null,
    val error: String? = null
)