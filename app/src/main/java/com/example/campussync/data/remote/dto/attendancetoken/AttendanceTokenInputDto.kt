package com.example.campussync.data.remote.dto.attendancetoken

import kotlinx.serialization.Serializable

@Serializable
data class AttendanceTokenInputDto(
    val lectureSessionId: Long
)
