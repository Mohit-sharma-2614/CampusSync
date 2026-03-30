package com.example.campussync.data.remote.dto.user

import com.example.campussync.data.remote.dto.enums.UserRole
import com.example.campussync.data.remote.dto.enums.UserStatus
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.sql.Timestamp

@Serializable
data class User(
    val id: Long,
    val name: String,
    val email: String,
    val role: UserRole,
    val status: UserStatus,
    @Contextual
    val createdAt: Timestamp
)