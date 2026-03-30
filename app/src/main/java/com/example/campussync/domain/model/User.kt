package com.example.campussync.domain.model

import com.example.campussync.data.remote.dto.enums.UserRole
import com.example.campussync.data.remote.dto.enums.UserStatus

sealed class User {
    data class Student(
        val id: Long,
        val name: String,
        val email: String,
        val jwtToke: String,
        val refreshToken: String,
    ) : User()

    data class Teacher(
        val id: Long,
        val name: String,
        val email: String,
        val jwtToke: String,
        val refreshToken: String,
    ) : User()
}