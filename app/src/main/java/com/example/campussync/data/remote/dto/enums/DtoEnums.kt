package com.example.campussync.data.remote.dto.enums

enum class UserStatus(val status: String) {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    SUSPENDED("Suspended")
}

enum class UserRole(val role: String) {
    STUDENT("Student"),
    TEACHER("Teacher"),
    ADMIN("Admin")
}

enum class AttendanceStatus(val status: String) {
    PRESENT("Present"),
    ABSENT("Absent"),
}