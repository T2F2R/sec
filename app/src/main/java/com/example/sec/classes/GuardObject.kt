package com.example.sec.classes

import java.io.Serializable

data class GuardObject(
    val id: Int,
    val name: String,
    val address: String,
    val description: String,
    val latitude: Double,
    val longitude: Double
) : java.io.Serializable {

    fun hasValidCoordinates(): Boolean {
        return latitude != 0.0 && longitude != 0.0 &&
                latitude >= -90 && latitude <= 90 &&
                longitude >= -180 && longitude <= 180
    }
}

data class ObjectsResponse(
    val success: Boolean,
    val objects: List<GuardObject> = emptyList(),
    val error: String? = null
)