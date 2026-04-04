package com.example.campussync.data.remote.dto.student

import kotlinx.serialization.Serializable

@Serializable
data class StudentInputDto(
    // User fields
    val name: String,
    val email: String,
    val password: String,

    // Student fields
    val rollNumber: String,
    val departmentId: String,
    val year: Int,
    val semester: Int,
    val section: String
)
