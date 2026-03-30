package com.example.campussync.data.remote.dto.teacher

import kotlinx.serialization.Serializable

@Serializable
data class TeacherInputDto(
    val name: String,
    val email: String,
    val password: String,
    val employeeId: String,
    val departmentId: String,
    val designation: String
)
