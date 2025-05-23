package com.example.campussync.ui.screens.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import com.example.campussync.ui.navigation.Routes
import com.example.campussync.ui.screens.dashboard.NavItem

fun getDefaultNavItems(): List<NavItem> = listOf(
    NavItem(Routes.HOME_NAV, Icons.Default.Home),
    NavItem(Routes.CLASSES_NAV, Icons.Default.CalendarToday),
    NavItem(Routes.PROFILE_NAV, Icons.Default.Person)
)