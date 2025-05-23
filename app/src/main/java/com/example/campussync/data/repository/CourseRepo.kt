package com.example.campussync.data.repository

import com.example.campussync.data.database.entitiy.CourseEntity

interface CourseRepo {
    suspend fun insertCourse(course: CourseEntity)
    suspend fun updateCourse(course: CourseEntity)
    suspend fun deleteCourse(course: CourseEntity)
    suspend fun getCoursesBySemester(semesterId: Long): List<CourseEntity>
    suspend fun getCourseById(courseId: Long): CourseEntity?
}