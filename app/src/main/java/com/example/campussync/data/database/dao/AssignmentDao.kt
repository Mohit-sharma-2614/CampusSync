package com.example.campussync.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.campussync.data.database.entitiy.AssignmentEntity

@Dao
interface AssignmentDao {
    @Insert
    suspend fun insertAssignment(assignment: AssignmentEntity)

    @Update
    suspend fun updateAssignment(assignment: AssignmentEntity)

    @Delete
    suspend fun deleteAssignment(assignment: AssignmentEntity)

    @Query("SELECT * FROM assignments WHERE assignment_id = :assignmentId")
    suspend fun getAssignmentById(assignmentId: Long): AssignmentEntity?

    @Query("SELECT * FROM assignments WHERE course_id = :courseId")
    suspend fun getAssignmentsByCourse(courseId: Long): List<AssignmentEntity>
}