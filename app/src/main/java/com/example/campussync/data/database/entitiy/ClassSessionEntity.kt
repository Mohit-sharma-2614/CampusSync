package com.example.campussync.data.database.entitiy

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime



@Entity(
    tableName = "class_sessions",
    indices = [Index("courses_id")],
    foreignKeys = [
        ForeignKey(
            entity = CourseEntity::class,
            parentColumns = ["course_id"],
            childColumns = ["courses_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ClassSessionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "session_id")
    val sessionId: Long = 0,
    @TypeConverters(LocalDateConverter::class) val date: LocalDate,
    @ColumnInfo(name = "start_time")
    @TypeConverters(LocalTimeConverter::class) val startTime: LocalTime,
    @ColumnInfo(name = "end_time")
    @TypeConverters(LocalTimeConverter::class) val endTime: LocalTime,
    val location: String?,
    @ColumnInfo(name = "courses_id")
    val courseId: Long
)


@RequiresApi(Build.VERSION_CODES.O)
class LocalDateConverter {
    @TypeConverter fun fromEpochDay(epochDay: Long): LocalDate = LocalDate.ofEpochDay(epochDay)
    @TypeConverter fun toEpochDay(date: LocalDate): Long = date.toEpochDay()
}
@RequiresApi(Build.VERSION_CODES.O)
class LocalTimeConverter {
    @TypeConverter fun fromMinutes(mins: Int): LocalTime = LocalTime.ofSecondOfDay(mins * 60L)
    @TypeConverter fun toMinutes(time: LocalTime): Int = time.toSecondOfDay() / 60
}
@RequiresApi(Build.VERSION_CODES.O)
class InstantConverter {
    @TypeConverter fun fromMillis(millis: Long): Instant = Instant.ofEpochMilli(millis)
    @TypeConverter fun toMillis(instant: Instant): Long = instant.toEpochMilli()
}