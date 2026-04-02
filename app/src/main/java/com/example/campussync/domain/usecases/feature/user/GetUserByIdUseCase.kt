package com.example.campussync.domain.usecases.feature.user

import com.example.campussync.data.manager.UserCredentialManager
import com.example.campussync.domain.usecases.base.BaseUseCase

class GetUserByIdUseCase(
    private val userCredentialManager: UserCredentialManager
): BaseUseCase<String, GetUserByIdUseCase.Params>() {

    override suspend fun buildUseCase(params: Params): String {
        return userCredentialManager.getUserId()
    }

    class Params private constructor(){
        companion object{
            fun forGetUserId() = Params()
        }
    }
}