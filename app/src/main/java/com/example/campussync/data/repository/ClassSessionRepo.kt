package com.example.campussync.data.repository

import com.example.campussync.data.database.entitiy.ClassSessionEntity

interface ClassSessionRepo {
    suspend fun insertClassSession(classSession: ClassSessionEntity)
    suspend fun updateClassSession(classSession: ClassSessionEntity)
    suspend fun deleteClassSession(classSession: ClassSessionEntity)
    suspend fun getClassSessionsByCourse(courseId: Long): List<ClassSessionEntity>
}