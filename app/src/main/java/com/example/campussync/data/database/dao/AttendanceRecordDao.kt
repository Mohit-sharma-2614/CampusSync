package com.example.campussync.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.campussync.data.database.entitiy.AttendanceRecordEntity

@Dao
interface AttendanceRecordDao {
    @Insert
    suspend fun insertAttendanceRecord(attendanceRecord: AttendanceRecordEntity)

    @Query("SELECT * FROM attendance_records WHERE course_id = :courseId")
    suspend fun getAttendanceRecordsByCourse(courseId: Long): List<AttendanceRecordEntity>

    @Query("SELECT * FROM attendance_records WHERE session_id = :sessionId")
    suspend fun getAttendanceRecordsBySession(sessionId: Long): List<AttendanceRecordEntity>

    @Update
    suspend fun updateAttendanceRecord(attendanceRecord: AttendanceRecordEntity)

    @Delete
    suspend fun deleteAttendanceRecord(attendanceRecord: AttendanceRecordEntity)


}