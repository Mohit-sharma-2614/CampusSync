package com.example.campussync.data.remote.dto.attendancetoken

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.sql.Timestamp
import java.util.UUID

@Serializable
data class AttendanceTokenDto(
    @Contextual
    val token: UUID,
    val lectureSessionId: Long,
    val generatedAt: Timestamp,
    val expiresAt: Timestamp
)
