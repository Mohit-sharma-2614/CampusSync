package com.example.campussync.data.database.entitiy

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "semesters",
    foreignKeys = [
        ForeignKey(
            entity = StudentsEntity::class,
            parentColumns = ["student_id"],
            childColumns = ["student_owner_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("student_owner_id")]
)
data class SemesterEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "semester_number")
    val semesterNumber: Int,
    val gpa: Double,
    @ColumnInfo(name =  "credits_earned")
    val creditsEarned: Int,
    @ColumnInfo(name = "credits_total")
    val creditsTotal: Int,
    @ColumnInfo(name = "student_owner_id")
    val studentOwnerId: String
)
