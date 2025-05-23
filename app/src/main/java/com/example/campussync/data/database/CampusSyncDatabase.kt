package com.example.campussync.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.campussync.data.database.dao.AssignmentDao
import com.example.campussync.data.database.dao.AttendanceRecordDao
import com.example.campussync.data.database.dao.ClassSessionDao
import com.example.campussync.data.database.dao.CourseDao
import com.example.campussync.data.database.dao.SemesterDao
import com.example.campussync.data.database.dao.StudentDao
import com.example.campussync.data.database.entitiy.AssignmentEntity
import com.example.campussync.data.database.entitiy.AttendanceRecordEntity
import com.example.campussync.data.database.entitiy.ClassSessionEntity
import com.example.campussync.data.database.entitiy.CourseEntity
import com.example.campussync.data.database.entitiy.InstantConverter
import com.example.campussync.data.database.entitiy.LocalDateConverter
import com.example.campussync.data.database.entitiy.LocalTimeConverter
import com.example.campussync.data.database.entitiy.SemesterEntity
import com.example.campussync.data.database.entitiy.StudentsEntity

@Database(entities = [
    StudentsEntity::class,
    SemesterEntity::class,
    CourseEntity::class,
    ClassSessionEntity::class,
    AssignmentEntity::class,
    AttendanceRecordEntity::class
    ],
    version = 1,
    exportSchema = true,
    )
@TypeConverters(
    LocalDateConverter::class,
    LocalTimeConverter::class,
    InstantConverter::class
)
public abstract class CampusSyncDatabase: RoomDatabase() {
    abstract fun studentDao(): StudentDao
    abstract fun semesterDao(): SemesterDao
    abstract fun courseDao(): CourseDao
    abstract fun classSessionDao(): ClassSessionDao
    abstract fun assignmentDao(): AssignmentDao
    abstract fun attendanceRecordDao(): AttendanceRecordDao

    companion object {
        @Volatile
        private var INSTANCE: CampusSyncDatabase? = null
        fun getDatabase(context: Context): CampusSyncDatabase? {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context = context, CampusSyncDatabase::class.java, "campus_sync_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also {
                        INSTANCE = it
                    }
            }
        }
    }

}