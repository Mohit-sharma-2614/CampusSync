package com.example.campussync.data.repository

import com.example.campussync.data.model.attendance.Attendance
import com.example.campussync.data.model.attendance.AttendanceReq
import com.example.campussync.utils.Resource
import kotlinx.coroutines.flow.Flow

interface AttendanceRepository {
    suspend fun getAllAttendance(): Resource<List<Attendance>>
    suspend fun getAttendanceById(attendanceId: Long): Resource<Attendance>
    suspend fun createAttendance(attendance: AttendanceReq): Resource<Attendance>
    suspend fun getAttendanceBySubjectId(subjectId: Long): Resource<List<Attendance>>

    suspend fun getAttendanceByStudentId(studentId: Long): Resource<List<Attendance>>

    suspend fun getAttendanceBySubjectAndStudentId(
        subjectId: Long,
        studentId: Long
    ): Resource<List<Attendance>>

    fun getAttendanceBySubjectAndDate(
        subjectId: Long,
        date: String
    ): Flow<Resource<List<Attendance>>>

    suspend fun createBulkAttendance(attendanceList: List<AttendanceReq>): Resource<List<Attendance>>

}