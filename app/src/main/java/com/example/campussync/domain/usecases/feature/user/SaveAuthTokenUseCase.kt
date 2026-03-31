package com.example.campussync.domain.usecases.feature.user

import android.util.Log
import com.example.campussync.data.manager.TokenManager
import com.example.campussync.data.remote.repository.UserRepo
import com.example.campussync.domain.model.User
import com.example.campussync.domain.usecases.base.BaseUseCase

class SaveAuthTokenUseCase(
    private val userRepo : UserRepo,
    private val tokenManager: TokenManager
): BaseUseCase<Boolean, SaveAuthTokenUseCase.Params>() {

    override suspend fun buildUseCase(params: Params): Boolean {
        try {
            val user = params.user
            if(user is User.Student) {
                tokenManager.saveToken(user.jwtToke)
                return true
            } else if(user is User.Teacher) {
                tokenManager.saveToken(user.jwtToke)
                return true
            }
        } catch (e: Exception){
            Log.e("SaveAuthTokenUseCase: failed to save token, error:", e.message.toString())
            return false
        }
        return false
    }

    class Params private constructor(val user: User) {
        companion object{
            fun forSaveAuthToken(user: User) = Params(user)
        }
    }
}