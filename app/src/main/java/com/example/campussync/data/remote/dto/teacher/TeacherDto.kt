package com.example.campussync.data.remote.dto.teacher

import kotlinx.serialization.Serializable

@Serializable
data class TeacherDto(
    val id: Long,
    val name: String,
    val email: String,
    val employeeId: String,
    val departmentId: String,
    val designation: String,

    val jwtToken: String,
    val refreshToken: String
)
