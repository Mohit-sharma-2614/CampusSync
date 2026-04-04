package com.example.campussync.domain.repository

import com.example.campussync.data.remote.api.UserApi
import com.example.campussync.data.remote.api.toDomain
import com.example.campussync.data.remote.dto.auth.AuthDto
import com.example.campussync.data.remote.dto.refreshtoken.RefreshTokenInputDto
import com.example.campussync.data.remote.dto.refreshtoken.RefreshTokenResponseDto
import com.example.campussync.data.remote.dto.user.UserDto
import com.example.campussync.data.remote.dto.user.UserLoginDto
import com.example.campussync.data.remote.repository.UserRepo
import com.example.campussync.domain.model.User

class UserRepoImpl(
    private val userApi: UserApi
) : UserRepo {

    override suspend fun getUserById(id: Long): UserDto {
        return userApi.getUserById(id)
    }

    override suspend fun loginStudent(loginDto: UserLoginDto): User{
        return userApi.loginStudent(loginDto).toDomain()
    }

    override suspend fun loginTeacher(loginDto: UserLoginDto): User {
        return userApi.loginTeacher(loginDto).toDomain()
    }

    override suspend fun logout(refreshTokenInputDto: RefreshTokenInputDto): AuthDto {
        return userApi.logOut(refreshTokenInputDto)
    }

    override suspend fun validateToken(): AuthDto {
        return userApi.validateToken()
    }

    override suspend fun refreshToken(refreshTokenInputDto: RefreshTokenInputDto): RefreshTokenResponseDto {
        return userApi.refreshToken(refreshTokenInputDto)
    }
}