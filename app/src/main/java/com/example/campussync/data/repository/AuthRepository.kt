package com.example.campussync.data.repository

import com.example.campussync.data.model.AuthToken
import com.example.campussync.utils.Resource

interface AuthRepository {
    suspend fun validateToken(token: String): Resource<AuthToken>
}