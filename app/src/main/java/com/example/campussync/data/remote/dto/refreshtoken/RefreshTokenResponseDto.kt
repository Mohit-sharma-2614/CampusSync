package com.example.campussync.data.remote.dto.refreshtoken

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenResponseDto(
    val accessToken: String,
    val refreshToken: String
)