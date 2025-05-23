package com.example.campussync.ui.screens.profile

import com.example.campussync.ui.screens.dashboard.NavItem
import com.example.campussync.ui.screens.utils.getDefaultNavItems

data class ProfileUiState(
    val name: String = "User",
    val avatar: Int = 0,
    val rollNumber: String = "",
    val branch: String = "",
    val semester: Int = 0,
    val attendancePercent: Int = 0,
    val cgpa: Double = 0.0,
    val creditsEarned: Int = 0,
    val quickActions: List<QuickAction> = emptyList(),
    val timeline: List<SemesterRecord> = emptyList(),
    val notifications: List<NotificationItem> = emptyList(),
    val selectedNavIndex: Int = 0,
    val navItems: List<NavItem> = getDefaultNavItems(),
)
