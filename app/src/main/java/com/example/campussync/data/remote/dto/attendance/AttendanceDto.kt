package com.example.campussync.data.remote.dto.attendance

import com.example.campussync.data.remote.dto.enums.AttendanceStatus
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.util.UUID

@Serializable
data class AttendanceDto(
    val id: Long,
    val enrollmentId: Long,
    val lectureSessionId: Long,
    @Contextual
    val tokenId: UUID,
    val status: AttendanceStatus,
    val createdAt: LocalDateTime
)
