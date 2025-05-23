package com.example.campussync.ui.screens.dashboard

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import com.example.campussync.R

data class DashboardUiState(
    val cards: List<DashboardCard> = emptyList(),
    val navItems: List<NavItem> = emptyList(),
    val selectedNavIndex: Int = 0
)
