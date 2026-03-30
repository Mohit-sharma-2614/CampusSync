package com.example.campussync.data.remote.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthDto(
    val valid: Boolean,
    val message: String
)
