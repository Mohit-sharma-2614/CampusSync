package com.example.campussync.domain.usecases.feature.user

import com.example.campussync.data.remote.dto.auth.AuthDto
import com.example.campussync.data.remote.dto.refreshtoken.RefreshTokenInputDto
import com.example.campussync.data.remote.repository.UserRepo
import com.example.campussync.domain.usecases.base.BaseUseCase

class LogOutUseCase(
    private val userRepo: UserRepo
): BaseUseCase<AuthDto, LogOutUseCase.Params>() {

    override suspend fun buildUseCase(params: Params): AuthDto {
        return userRepo.logout(params.refreshTokenInputDto)
    }

    class Params private constructor(val refreshTokenInputDto: RefreshTokenInputDto){
        companion object{
            fun forLogOut(refreshTokenInputDto: RefreshTokenInputDto) = Params(refreshTokenInputDto)
        }
    }
}