package com.example.campussync.data.remote.dto.user

import com.example.campussync.data.remote.dto.enums.UserRole
import com.example.campussync.data.remote.dto.enums.UserStatus
import com.example.campussync.utils.InstantSerializer
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
data class UserDto @OptIn(ExperimentalTime::class) constructor(
    val id: Long?,
    val name: String,
    val email: String,
    val role: UserRole?,
    val status: UserStatus?,
    @Serializable(with = InstantSerializer::class)
    val createdAt: Instant?
)
