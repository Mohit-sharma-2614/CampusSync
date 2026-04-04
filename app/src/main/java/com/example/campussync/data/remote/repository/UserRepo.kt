package com.example.campussync.data.remote.repository

import com.example.campussync.data.remote.dto.auth.AuthDto
import com.example.campussync.data.remote.dto.refreshtoken.RefreshTokenInputDto
import com.example.campussync.data.remote.dto.refreshtoken.RefreshTokenResponseDto
import com.example.campussync.data.remote.dto.user.UserDto
import com.example.campussync.data.remote.dto.user.UserLoginDto
import com.example.campussync.domain.model.User

interface UserRepo {

    suspend fun getUserById(id: Long): UserDto
    suspend fun loginStudent(loginDto: UserLoginDto): User
    suspend fun loginTeacher(loginDto: UserLoginDto): User

    suspend fun logout(refreshTokenInputDto: RefreshTokenInputDto): AuthDto

    suspend fun validateToken(): AuthDto
    suspend fun refreshToken(refreshTokenInputDto: RefreshTokenInputDto): RefreshTokenResponseDto
}

