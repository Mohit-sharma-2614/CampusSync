package com.example.campussync.domain.usecases.feature.user

import android.util.Log
import com.example.campussync.data.manager.TokenManager
import com.example.campussync.data.remote.repository.UserRepo
import com.example.campussync.domain.usecases.base.BaseUseCase

class SaveTokenUseCase(
    private val userRepo : UserRepo,
    private val tokenManager: TokenManager
): BaseUseCase<Boolean, SaveTokenUseCase.Params>() {

    override suspend fun buildUseCase(params: Params): Boolean {
        try {
            val token = params.token
            if (params.isRefreshToken){
                tokenManager.saveRefreshToken(token)
            } else {
                tokenManager.saveToken(token)
            }
            return true
        } catch (e: Exception){
            Log.e("SaveAuthTokenUseCase: failed to save token, error:", e.message.toString())
            return false
        }
    }

    class Params private constructor(val token: String, val isRefreshToken: Boolean) {
        companion object{
            fun forSaveToken(token: String, isRefreshToken: Boolean = false) = Params(token, isRefreshToken)
        }
    }
}