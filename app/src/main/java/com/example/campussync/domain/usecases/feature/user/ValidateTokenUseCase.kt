package com.example.campussync.domain.usecases.feature.user

import com.example.campussync.data.remote.dto.refreshtoken.RefreshTokenResponseDto
import com.example.campussync.data.remote.repository.UserRepo
import com.example.campussync.domain.usecases.base.BaseUseCase

class ValidateTokenUseCase(
    private val userRepo: UserRepo
) : BaseUseCase<RefreshTokenResponseDto, ValidateTokenUseCase.Params>(){
    override suspend fun buildUseCase(params: Params): RefreshTokenResponseDto {
        return userRepo.refreshToken()
    }

    class Params private constructor(
        val id: Long
    ){
        companion object{
            fun validateToken(id: Long) = Params(id)
        }
    }
}