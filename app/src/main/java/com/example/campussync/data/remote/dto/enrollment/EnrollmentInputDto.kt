package com.example.campussync.data.remote.dto.enrollment

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class EnrollmentInputDto(
    val studentId: Long,
    val courseOfferingId: Long,
    @Contextual
    val createdAt: LocalDateTime?
)
