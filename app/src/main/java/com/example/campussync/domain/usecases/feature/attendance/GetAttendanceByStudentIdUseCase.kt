package com.example.campussync.domain.usecases.feature.attendance

import com.example.campussync.data.remote.dto.enums.AttendanceStatus
import com.example.campussync.data.remote.repository.AttendanceRepo
import com.example.campussync.data.remote.repository.EnrollmentRepo
import com.example.campussync.domain.usecases.base.BaseUseCase
import java.time.LocalDateTime

class GetAttendanceByStudentIdUseCase(
    private val enrollmentRepo: EnrollmentRepo,
    private val attendanceRepo: AttendanceRepo,

): BaseUseCase<List<GetAttendanceByStudentIdUseCase.Attendance>, GetAttendanceByStudentIdUseCase.Params>() {

    override suspend fun buildUseCase(params: Params): List<Attendance> {
        val enrollments = enrollmentRepo.getEnrollmentByStudentId(params.studentId)
        val attendance = enrollments.flatMap { enrollment ->
            attendanceRepo.getAttendanceByEnrollmentId(enrollment.id)
        }
        return attendance.map { attendance ->
            Attendance(
                id = attendance.id,
                date = attendance.createdAt,
                status = attendance.status
            )
        }
    }

    data class Attendance(
        val id: Long,
        val date: LocalDateTime,
        val status: AttendanceStatus,
    )

    class Params private constructor(val studentId: Long){
        companion object{
            fun forGetAttendanceByStudentId(studentId: Long) = Params(studentId)
        }
    }
}