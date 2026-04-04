package com.example.campussync.domain.usecases.feature.user

import com.example.campussync.data.remote.dto.user.UserDto
import com.example.campussync.data.remote.repository.UserRepo
import com.example.campussync.domain.usecases.base.BaseUseCase

class GetUserByIdUseCase(
    private val repo: UserRepo
): BaseUseCase<UserDto, GetUserByIdUseCase.Params>() {

    override suspend fun buildUseCase(params: Params): UserDto {
        return repo.getUserById(params.userId.toLong())
    }

    class Params private constructor(val userId: String){
        companion object{
            fun forGetUserById(userId: String) = Params(userId)
        }
    }
}