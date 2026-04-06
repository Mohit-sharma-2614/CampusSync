package com.example.campussync.data.remote.dto.subject

import kotlinx.serialization.Serializable

@Serializable
data class SubjectInputDto(
    val name: String,
    val code: String,
    val credits: Int,
    val departmentId: Long
)
