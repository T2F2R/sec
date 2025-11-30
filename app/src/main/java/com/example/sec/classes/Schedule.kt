package com.example.sec.classes

import com.google.gson.annotations.SerializedName

/**
 * Универсальная модель смены (и для расписания, и для истории)
 */
data class ScheduleItem(
    val id: Int,

    @SerializedName("date")
    val date: String,

    @SerializedName("start_time")
    val startTime: String,

    @SerializedName("end_time")
    val endTime: String,

    val notes: String? = null,

    @SerializedName("object_name")
    val objectName: String,

    @SerializedName("object_address")
    val objectAddress: String,

    @SerializedName("guard_object_id")
    val guardObjectId: Int = 0
)

/**
 * Единый формат ответа сервера — под историю И под расписание.
 *
 * ВАЖНО:
 *  - твой бекенд возвращает поле "schedule"
 *  - мои исходные классы использовали "schedules"
 *
 * Я привожу их к твоему формату.
 */
data class ScheduleResponse(
    val success: Boolean,
    val history: List<ScheduleItem>? = emptyList(), // Изменили schedule на history
    val schedule: List<ScheduleItem>? = emptyList(), // Оставляем для обратной совместимости
    val error: String? = null
)
