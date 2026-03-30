package com.example.campussync.data.remote.dto.refreshtoken

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.sql.Timestamp

@Serializable
data class RefreshTokenInputDto(
    val id: Long,
    val userId: Long,
    val token: String,
    @Contextual
    val issuedAt: Timestamp,
    val expiresAt: Timestamp,
    val revokedAt: Timestamp,
    val deviceInfo: String
)
