package com.example.campussync.data.remote.dto.attendancetoken

import com.example.campussync.utils.TimestampSerializer
import com.example.campussync.utils.UUIDSerializer
import kotlinx.serialization.Serializable
import java.sql.Timestamp
import java.util.UUID

@Serializable
data class AttendanceTokenDto(
    @Serializable(with = UUIDSerializer::class)
    val token: UUID,
    val lectureSessionId: Long,
    @Serializable(with = TimestampSerializer::class)
    val generatedAt: Timestamp,
    @Serializable(with = TimestampSerializer::class)
    val expiresAt: Timestamp
)
