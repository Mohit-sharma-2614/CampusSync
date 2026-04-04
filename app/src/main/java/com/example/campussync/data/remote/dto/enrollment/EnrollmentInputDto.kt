package com.example.campussync.data.remote.dto.enrollment

import com.example.campussync.utils.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class EnrollmentInputDto(
    val studentId: Long,
    val courseOfferingId: Long,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime?
)
