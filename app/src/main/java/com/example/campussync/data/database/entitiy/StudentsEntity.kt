package com.example.campussync.data.database.entitiy

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class StudentsEntity(
    @PrimaryKey
    @ColumnInfo(name = "student_id")
    val studentId: String, // roll‑number or UUID
    val name: String,
    val branch: String,
    @ColumnInfo(name = "avatar_uri")
    val avatarUri: String?,
    @ColumnInfo(name = "current_semester")
    val currentSemester: Int,
    val email: String,
    val phone: String
)
