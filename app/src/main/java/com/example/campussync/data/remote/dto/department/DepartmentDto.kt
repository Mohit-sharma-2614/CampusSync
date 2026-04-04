package com.example.campussync.data.remote.dto.department

import kotlinx.serialization.Serializable

@Serializable
data class DepartmentDto(
    val id: Long,
    val name: String
)
