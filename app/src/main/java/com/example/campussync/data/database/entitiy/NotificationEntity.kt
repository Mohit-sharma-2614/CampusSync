package com.example.campussync.data.database.entitiy

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.time.Instant

@RequiresApi(Build.VERSION_CODES.O)
@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subtitle: String,
    @ColumnInfo(name = "icon_name")
    val iconName: String, // store Material icon name or custom asset key
    @TypeConverters(InstantConverter::class)
    @ColumnInfo(name = "created_at")
    val createdAt: Instant = Instant.now(),
    val read: Boolean = false,
    @ColumnInfo(name = "student_id")
    val studentId: String
)
