package com.example.campussync.data.repository.local

import com.example.campussync.data.database.dao.CourseDao
import com.example.campussync.data.database.entitiy.CourseEntity
import com.example.campussync.data.repository.CourseRepo

class OfflineCourseRepo(
    private val offlineCourseDao: CourseDao
): CourseRepo {
    override suspend fun insertCourse(course: CourseEntity) = offlineCourseDao.insertCourse(course)
    override suspend fun updateCourse(course: CourseEntity) = offlineCourseDao.updateCourse(course)
    override suspend fun deleteCourse(course: CourseEntity) = offlineCourseDao.deleteCourse(course)
    override suspend fun getCourseById(courseId: Long): CourseEntity? = offlineCourseDao.getCourseById(courseId)
    override suspend fun getCoursesBySemester(semesterId: Long): List<CourseEntity> = offlineCourseDao.getCoursesBySemester(semesterId)
}