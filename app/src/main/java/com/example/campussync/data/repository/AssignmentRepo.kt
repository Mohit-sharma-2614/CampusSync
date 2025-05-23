package com.example.campussync.data.repository

import com.example.campussync.data.database.entitiy.AssignmentEntity


interface AssignmentRepo {
    suspend fun insertAssignment(assignment: AssignmentEntity)
    suspend fun updateAssignment(assignment: AssignmentEntity)
    suspend fun deleteAssignment(assignment: AssignmentEntity)
    suspend fun getAssignmentById(assignmentId: Long): AssignmentEntity?
    suspend fun getAssignmentsByCourse(courseId: Long): List<AssignmentEntity>
}