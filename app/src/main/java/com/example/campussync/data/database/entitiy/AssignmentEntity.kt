package com.example.campussync.data.database.entitiy

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.time.LocalDate

@Entity(
    tableName = "assignments",
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
data class AssignmentEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "assignment_id")
    val assignmentId: Long = 0,
    val title: String,
    val description: String?,
    @ColumnInfo(name = "due_date")
    @TypeConverters(LocalDateConverter::class) val dueDate: LocalDate,
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean = false,
    @ColumnInfo(name = "course_id")
    val courseId: Long
)
