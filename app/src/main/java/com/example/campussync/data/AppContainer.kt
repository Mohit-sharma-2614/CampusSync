package com.example.campussync.data

import android.content.Context
import com.example.campussync.data.database.CampusSyncDatabase
import com.example.campussync.data.repository.AssignmentRepo
import com.example.campussync.data.repository.AttendanceRecordRepo
import com.example.campussync.data.repository.ClassSessionRepo
import com.example.campussync.data.repository.CourseRepo
import com.example.campussync.data.repository.SemesterRepo
import com.example.campussync.data.repository.StudentRepo
import com.example.campussync.data.repository.local.OfflineAssignmentRepo
import com.example.campussync.data.repository.local.OfflineAttendanceRecordRepo
import com.example.campussync.data.repository.local.OfflineClassSessionRepo
import com.example.campussync.data.repository.local.OfflineCourseRepo
import com.example.campussync.data.repository.local.OfflineSemesterRepo
import com.example.campussync.data.repository.local.OfflineStudentRepo

interface AppContainer{
    val studentRepo: StudentRepo
    val semesterRepo: SemesterRepo
    val courseRepo: CourseRepo
    val classSessionRepo: ClassSessionRepo
    val assignmentRepo: AssignmentRepo
    val attendanceRecordRepo: AttendanceRecordRepo
}

class AppDataContainer(private val context: Context): AppContainer{
    private val database: CampusSyncDatabase by lazy{
        CampusSyncDatabase.getDatabase(context)!!
    }
    override val semesterRepo: SemesterRepo by lazy {
        OfflineSemesterRepo(database.semesterDao())
    }
    override val studentRepo: StudentRepo by lazy {
        OfflineStudentRepo(database.studentDao())
    }
    override val courseRepo: CourseRepo by lazy {
        OfflineCourseRepo(database.courseDao())
    }
    override val classSessionRepo: ClassSessionRepo by lazy {
        OfflineClassSessionRepo(database.classSessionDao())
    }
    override val assignmentRepo: AssignmentRepo by lazy {
        OfflineAssignmentRepo(database.assignmentDao())
    }
    override val attendanceRecordRepo: AttendanceRecordRepo by lazy {
        OfflineAttendanceRecordRepo(database.attendanceRecordDao())
    }
}