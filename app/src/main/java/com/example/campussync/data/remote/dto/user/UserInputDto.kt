package com.example.campussync.data.remote.dto.user

import com.example.campussync.data.remote.dto.enums.UserRole
import com.example.campussync.data.remote.dto.enums.UserStatus
import kotlinx.serialization.Serializable

@Serializable
data class UserInputDto(
    val id: Long,
    val name: String,
    val email: String,
    val password: String,
    val role: UserRole,
    val status: UserStatus
)
