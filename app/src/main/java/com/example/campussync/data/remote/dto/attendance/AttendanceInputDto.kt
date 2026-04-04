package com.example.campussync.data.remote.dto.attendance

import com.example.campussync.data.remote.dto.enums.AttendanceStatus
import kotlinx.serialization.Serializable

@Serializable
data class AttendanceInputDto(
    val enrollmentId: Long,
    val lectureSessionId: Long,
    val tokenId: String,
    val status: AttendanceStatus
)
