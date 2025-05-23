package com.example.campussync.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.campussync.CampusSyncApplication
import com.example.campussync.ui.screens.attendence.AttendanceViewModel
import com.example.campussync.ui.screens.attendence.MarkAttendanceViewModel
import com.example.campussync.ui.screens.classes.ClassesViewModel
import com.example.campussync.ui.screens.dashboard.DashboardViewModel
import com.example.campussync.ui.screens.profile.ProfileViewModel


object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            val container = campusSyncApplication().container
            AttendanceViewModel(
                this.createSavedStateHandle(),
                container.studentRepo,
                container.semesterRepo,
                container.courseRepo,
                container.classSessionRepo,
                container.assignmentRepo,
                container.attendanceRecordRepo
            )
        }

        initializer {
            val container = campusSyncApplication().container
            MarkAttendanceViewModel(
                this.createSavedStateHandle(),
                container.studentRepo,
                container.semesterRepo,
                container.courseRepo,
                container.classSessionRepo,
                container.assignmentRepo,
                container.attendanceRecordRepo
            )
        }

        initializer {
            val container = campusSyncApplication().container
            DashboardViewModel(
                this.createSavedStateHandle(),
                container.studentRepo,
                container.semesterRepo,
                container.courseRepo,
                container.classSessionRepo,
                container.assignmentRepo,
                container.attendanceRecordRepo
            )
        }

        initializer {
            val container = campusSyncApplication().container
            ProfileViewModel(
                this.createSavedStateHandle(),
                container.studentRepo,
                container.semesterRepo,
                container.courseRepo,
                container.classSessionRepo,
                container.assignmentRepo,
                container.attendanceRecordRepo
            )
        }
        initializer {
            val container = campusSyncApplication().container
            ClassesViewModel(
                this.createSavedStateHandle(),
                container.studentRepo,
                container.semesterRepo,
                container.courseRepo,
                container.classSessionRepo,
                container.assignmentRepo,
                container.attendanceRecordRepo
            )
        }
    }
}

fun CreationExtras.campusSyncApplication(): CampusSyncApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as CampusSyncApplication)