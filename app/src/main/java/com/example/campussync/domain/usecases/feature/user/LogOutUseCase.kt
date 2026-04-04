package com.example.campussync.domain.usecases.feature.user

import com.example.campussync.data.manager.TokenManager
import com.example.campussync.data.manager.UserCredentialManager
import com.example.campussync.data.remote.dto.auth.AuthDto
import com.example.campussync.data.remote.dto.refreshtoken.RefreshTokenInputDto
import com.example.campussync.data.remote.repository.UserRepo
import com.example.campussync.domain.usecases.base.BaseUseCase

class LogOutUseCase(
    private val userRepo: UserRepo,
    private val tokenManager: TokenManager,
    private val userCredentialManager: UserCredentialManager
): BaseUseCase<AuthDto, LogOutUseCase.Params>() {

    override suspend fun buildUseCase(params: Params): AuthDto {
        tokenManager.clearToken()
        tokenManager.clearRefreshToken()
        userCredentialManager.clearUserId()
        return userRepo.logout(params.refreshTokenInputDto)
    }

    class Params private constructor(val refreshTokenInputDto: RefreshTokenInputDto){
        companion object{
            fun forLogOut(refreshTokenInputDto: RefreshTokenInputDto) = Params(refreshTokenInputDto)
        }
    }
}