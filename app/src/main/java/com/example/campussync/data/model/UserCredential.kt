package com.example.campussync.data.model

data class UserCredential(
    val isTeacher: Boolean = false,
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val token: String = "",
    val department: String = ""
)
