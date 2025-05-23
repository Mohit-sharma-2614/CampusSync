package com.example.campussync.data.repository.local

import com.example.campussync.data.database.dao.AttendanceRecordDao
import com.example.campussync.data.database.entitiy.AttendanceRecordEntity
import com.example.campussync.data.repository.AttendanceRecordRepo

class OfflineAttendanceRecordRepo(
    private val attendanceRecordDao: AttendanceRecordDao
): AttendanceRecordRepo {
    override suspend fun insertAttendanceRecord(attendanceRecord: AttendanceRecordEntity) = attendanceRecordDao.insertAttendanceRecord(attendanceRecord)

    override suspend fun updateAttendanceRecord(attendanceRecord: AttendanceRecordEntity) = attendanceRecordDao.updateAttendanceRecord(attendanceRecord)

    override suspend fun deleteAttendanceRecord(attendanceRecord: AttendanceRecordEntity) = attendanceRecordDao.deleteAttendanceRecord(attendanceRecord)

    override suspend fun getAttendanceRecordsByCourse(courseId: Long): List<AttendanceRecordEntity> = attendanceRecordDao.getAttendanceRecordsByCourse(courseId)

    override suspend fun getAttendanceRecordsBySession(sessionId: Long): List<AttendanceRecordEntity> = attendanceRecordDao.getAttendanceRecordsBySession(sessionId)
}