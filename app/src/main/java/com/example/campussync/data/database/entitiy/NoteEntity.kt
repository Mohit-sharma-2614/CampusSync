package com.example.campussync.data.database.entitiy

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.time.Instant

@Entity(
    tableName = "notes",
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

@RequiresApi(Build.VERSION_CODES.O)
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "note_id")
    val noteId: Long = 0,
    val title: String,
    val content: String,
    @TypeConverters(InstantConverter::class) val createdAt: Instant = Instant.now(),
    @ColumnInfo(name = "course_id")
    val courseId: Long
)
