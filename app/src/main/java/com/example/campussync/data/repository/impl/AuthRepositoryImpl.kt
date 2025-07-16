package com.example.campussync.data.repository.impl

import android.util.Log
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
            Log.d( "AuthRepositoryImpl",  "validateToken called with token: |$token|" )
            val response = apiService.validateToken("Bearer $token")

            Log.d("AuthRepositoryImpl", "validateToken response: ${response.body()?.valid}")

            if (response.isSuccessful) {
                response.body()?.let { authToken ->
                    // Backend returns { "valid": true/false, "message": "..." } with HTTP 200
                    if (authToken.valid) { // Check the 'valid' field from the parsed body
                        Resource.Success(authToken)
                    } else {
                        Resource.Error(response.body()?.message ?: "Token explicitly invalid by server (HTTP 200).")
                    }
                } ?: Resource.Error("Server returned success but no token body.")
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    "HTTP ${response.code()}: ${errorBody ?: response.message()}"
                } catch (e: Exception) {
                    "HTTP ${response.code()}: ${response.message()}" // Fallback
                }
                Resource.Error(errorMessage)
            }
        } catch (e: Exception) {
            Resource.Error("Network/connectivity error: ${e.message ?: "Unknown network error"}")
        }
    }
}