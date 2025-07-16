package com.example.campussync.data.model

// AuthToken model to verify token on server side this is the return type of AuthApiService

data class AuthToken(
    val valid: Boolean,
    val message: String
)
