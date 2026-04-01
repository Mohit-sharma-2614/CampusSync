package com.example.campussync.domain.usecases.feature.user

import com.example.campussync.data.remote.dto.auth.AuthDto
import com.example.campussync.data.remote.repository.UserRepo
import com.example.campussync.domain.usecases.base.BaseUseCase

class ValidateTokenUseCase(
    private val userRepo: UserRepo
) : BaseUseCase<AuthDto, ValidateTokenUseCase.Params>(){
    override suspend fun buildUseCase(params: Params): AuthDto {
        return userRepo.validateToken()
    }

    class Params private constructor(){
        companion object{
            fun validateToken() = Params()
        }
    }
}