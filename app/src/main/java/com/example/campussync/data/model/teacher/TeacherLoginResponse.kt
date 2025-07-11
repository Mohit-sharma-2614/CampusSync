package com.example.campussync.data.model.teacher

import com.google.gson.annotations.SerializedName

data class TeacherLoginResponse(
    val id: Long,
    val name: String,
    val email: String,
    @SerializedName("jwtToken")
    val token: String,
    @SerializedName("departmentName")
    val department: String
)
