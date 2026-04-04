package com.example.campussync.data.remote.dto.enrollment

import kotlinx.serialization.Serializable

@Serializable
data class EnrollmentDto(
    val id: Long,
    val studentId: Long,
    val courseOfferingId: Long
)
