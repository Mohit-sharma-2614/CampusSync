package com.example.campussync.data.database.entitiy

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.time.LocalDate

@Entity(
    tableName = "exams",
    indices = [Index("course_id")],
    foreignKeys = [
        ForeignKey(
            entity = CourseEntity::class,
            parentColumns = ["course_id"],
            childColumns = ["course_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ExamEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "exam_id")
    val examId: Long = 0,
    @TypeConverters(LocalDateConverter::class) val date: LocalDate,
    val venue: String,
    val description: String?,
    @ColumnInfo(name = "course_id")
    val courseId: Long
)
