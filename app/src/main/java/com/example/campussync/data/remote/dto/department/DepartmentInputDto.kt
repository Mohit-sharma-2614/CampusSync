package com.example.campussync.data.remote.dto.department

import kotlinx.serialization.Serializable

@Serializable
data class DepartmentInputDto(
    val name: String,
    val code: String
)
