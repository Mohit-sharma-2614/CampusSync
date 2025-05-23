package com.example.campussync.data.repository.local

import com.example.campussync.data.database.dao.AssignmentDao
import com.example.campussync.data.database.entitiy.AssignmentEntity
import com.example.campussync.data.repository.AssignmentRepo

class OfflineAssignmentRepo(
    private val assignmentDao: AssignmentDao
): AssignmentRepo {
    override suspend fun insertAssignment(assignment: AssignmentEntity) = assignmentDao.insertAssignment(assignment)

    override suspend fun updateAssignment(assignment: AssignmentEntity) = assignmentDao.updateAssignment(assignment)

    override suspend fun deleteAssignment(assignment: AssignmentEntity) = assignmentDao.deleteAssignment(assignment)

    override suspend fun getAssignmentById(assignmentId: Long): AssignmentEntity? = assignmentDao.getAssignmentById(assignmentId)

    override suspend fun getAssignmentsByCourse(courseId: Long): List<AssignmentEntity> = assignmentDao.getAssignmentsByCourse(courseId)
}