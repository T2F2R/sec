package com.example.sec.classes

data class GuardObject(
    val id: Int,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val description: String
)

data class ObjectsResponse(
    val success: Boolean,
    val objects: List<GuardObject> = emptyList(),
    val error: String? = null
)