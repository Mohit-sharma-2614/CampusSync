package com.example.campussync.data.repository.local

import com.example.campussync.data.database.dao.ClassSessionDao
import com.example.campussync.data.database.entitiy.ClassSessionEntity
import com.example.campussync.data.repository.ClassSessionRepo

class OfflineClassSessionRepo(
    private val classSessionDao: ClassSessionDao
): ClassSessionRepo {
    override suspend fun insertClassSession(classSession: ClassSessionEntity) = classSessionDao.insertClassSession(classSession)

    override suspend fun updateClassSession(classSession: ClassSessionEntity) = classSessionDao.updateClassSession(classSession)

    override suspend fun deleteClassSession(classSession: ClassSessionEntity) = classSessionDao.deleteClassSession(classSession)

    override suspend fun getClassSessionsByCourse(courseId: Long): List<ClassSessionEntity> = classSessionDao.getClassSessionsByCourse(courseId)
}