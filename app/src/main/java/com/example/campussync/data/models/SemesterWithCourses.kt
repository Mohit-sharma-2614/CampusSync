package com.example.campussync.data.models

import androidx.room.Embedded
import androidx.room.Relation
import com.example.campussync.data.database.entitiy.CourseEntity
import com.example.campussync.data.database.entitiy.SemesterEntity
import com.example.campussync.data.database.entitiy.StudentsEntity

data class SemesterWithCourses(
    @Embedded val semester: SemesterEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "semester_id"
    )
    val courses: List<CourseEntity>
)