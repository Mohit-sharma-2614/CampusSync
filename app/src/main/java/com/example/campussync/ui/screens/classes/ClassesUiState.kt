package com.example.campussync.ui.screens.classes

import androidx.compose.ui.graphics.Color
import com.example.campussync.ui.screens.dashboard.NavItem
import com.example.campussync.ui.screens.utils.getDefaultNavItems


data class ClassesUiState(
    val days: List<DaySchedule> = emptyList(),
    val selectedDayIndex: Int = 0,
    val selectedNavIndex: Int = 0,
    val navItems: List<NavItem> = getDefaultNavItems()
)