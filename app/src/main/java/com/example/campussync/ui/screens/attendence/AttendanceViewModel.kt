package com.example.campussync.ui.screens.attendence

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.campussync.data.repository.AssignmentRepo
import com.example.campussync.data.repository.AttendanceRecordRepo
import com.example.campussync.data.repository.ClassSessionRepo
import com.example.campussync.data.repository.CourseRepo
import com.example.campussync.data.repository.SemesterRepo
import com.example.campussync.data.repository.StudentRepo

class AttendanceViewModel(
    savedStateHandle: SavedStateHandle,
    private val studentRepo: StudentRepo,
    private val semesterRepo: SemesterRepo,
    private val courseRepo: CourseRepo,
    private val classSessionRepo: ClassSessionRepo,
    private val assignmentRepo: AssignmentRepo,
    private val attendanceRecordRepo: AttendanceRecordRepo,
) : ViewModel() {

}