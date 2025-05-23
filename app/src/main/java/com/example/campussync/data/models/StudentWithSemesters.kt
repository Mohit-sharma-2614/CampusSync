package com.example.campussync.data.models

import androidx.room.Embedded
import androidx.room.Relation
import com.example.campussync.data.database.entitiy.SemesterEntity
import com.example.campussync.data.database.entitiy.StudentsEntity

data class StudentWithSemesters (
    @Embedded val student: StudentsEntity,
    @Relation(
        parentColumn = "student_id",
        entityColumn = "student_owner_id"
    )
    val semesters: List<SemesterEntity>
)