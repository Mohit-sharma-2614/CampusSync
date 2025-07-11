package com.example.campussync.data.repository.impl

import com.example.campussync.api.AuthApiService
import com.example.campussync.data.model.AuthToken
import com.example.campussync.data.repository.AuthRepository
import com.example.campussync.utils.Resource
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: AuthApiService
) : AuthRepository {
    override suspend fun validateToken(token: String): Resource<AuthToken> {
        return try {
            val response = apiService.validateToken("Bearer $token")
            response.body()?.let {
                return@let Resource.Success(it)
            } ?: Resource.Error("Failed to validate token")
        } catch (e: Exception) {
            Resource.Error("Network error: ${e.message}")
        }
    }
}