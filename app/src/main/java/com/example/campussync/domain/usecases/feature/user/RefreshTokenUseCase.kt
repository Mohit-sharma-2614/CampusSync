package com.example.campussync.domain.usecases.feature.user

import com.example.campussync.data.remote.dto.refreshtoken.RefreshTokenInputDto
import com.example.campussync.data.remote.dto.refreshtoken.RefreshTokenResponseDto
import com.example.campussync.data.remote.repository.UserRepo
import com.example.campussync.domain.usecases.base.BaseUseCase

class RefreshTokenUseCase(
    private val userRepo: UserRepo
): BaseUseCase<RefreshTokenResponseDto, RefreshTokenUseCase.Params>() {
    override suspend fun buildUseCase(params: Params): RefreshTokenResponseDto {
        return userRepo.refreshToken(params.refreshTokenInputDto)
    }

    class Params private constructor(
        val refreshTokenInputDto: RefreshTokenInputDto
    ){
        companion object{
            fun forRefreshToken(refreshTokenInputDto: RefreshTokenInputDto) = Params(refreshTokenInputDto)
        }
    }
}