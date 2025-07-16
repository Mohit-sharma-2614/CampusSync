package com.example.campussync.data.model.attendance


// Used to create attendance
data class AttendanceReq(

    val studentId: Long,
    val subjectId: Long,
    val status: String

)
