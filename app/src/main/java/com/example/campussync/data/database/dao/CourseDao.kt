package com.example.campussync.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.campussync.data.database.entitiy.CourseEntity

@Dao
interface CourseDao {
    @Insert
    suspend fun insertCourse(course: CourseEntity)

    @Update
    suspend fun updateCourse(course: CourseEntity)

    @Delete
    suspend fun deleteCourse(course: CourseEntity)

    @Query("SELECT * FROM courses WHERE semester_id = :semesterId")
    suspend fun getCoursesBySemester(semesterId: Long): List<CourseEntity>

    @Query("SELECT * FROM courses WHERE course_id = :courseId")
    suspend fun getCourseById(courseId: Long): CourseEntity?
}
