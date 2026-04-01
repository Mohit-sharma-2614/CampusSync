package com.example.campussync.data.remote.dto.refreshtoken

import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenInputDto(
//    val id: Long,
    val userId: Long,
    val token: String,
    // I don't see any use of issuedAt, expiresAt, revokedAt
    // These can be accessed in the backend.
//    @Contextual
//    val issuedAt: Timestamp,
//    val expiresAt: Timestamp,
//    val revokedAt: Timestamp,
    val deviceInfo: String
)
