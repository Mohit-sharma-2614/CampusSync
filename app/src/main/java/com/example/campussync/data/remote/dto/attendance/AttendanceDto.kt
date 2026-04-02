package com.example.campussync.data.remote.dto.attendance

import com.example.campussync.data.remote.dto.enums.AttendanceStatus
import com.example.campussync.utils.LocalDateTimeSerializer
import com.example.campussync.utils.UUIDSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.UUID

@Serializable
data class AttendanceDto(
    val id: Long,
    val enrollmentId: Long,
    val lectureSessionId: Long,
    @Serializable(with = UUIDSerializer::class)
    val tokenId: UUID,
    val status: AttendanceStatus,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime
)
