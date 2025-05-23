package com.example.campussync.data.database.entitiy

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "courses",
    indices = [Index("semester_id")],
    foreignKeys = [
        ForeignKey(
            entity = SemesterEntity::class,
            parentColumns = ["id"],
            childColumns = ["semester_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CourseEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "course_id")
    val courseId: Long = 0,
    val name: String,
    val code: String,
    val lecturer: String,
    val color: Long, // ARGB hex packed in Long
    val location: String?,
    @ColumnInfo(name = "semester_id")
    val semesterId: Long
)
