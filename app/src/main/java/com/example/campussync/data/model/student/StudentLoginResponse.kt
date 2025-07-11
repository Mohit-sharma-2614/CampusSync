package com.example.campussync.data.model.student

import com.google.gson.annotations.SerializedName

data class StudentLoginResponse(
    val id: Long,
    val name: String,
    @SerializedName("student_uid")
    val studentUid: String,
    val email: String,
    @SerializedName("jwt_token")
    val token: String,
    val semester: Int
)
