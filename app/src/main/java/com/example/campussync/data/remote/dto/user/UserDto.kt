package com.example.campussync.data.remote.dto.user

import com.example.campussync.data.remote.dto.enums.UserRole
import com.example.campussync.data.remote.dto.enums.UserStatus
import com.example.campussync.utils.TimestampSerializer
import kotlinx.serialization.Serializable
import java.sql.Timestamp

@Serializable
data class UserDto(
    val id: Long?,
    val name: String,
    val email: String,
    val role: UserRole?,
    val status: UserStatus?,
    @Serializable(with = TimestampSerializer::class)
    val createdAt: Timestamp?
)
