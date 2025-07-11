package com.example.campussync.persentation.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.campussync.navigation.Routes

@Composable
fun BottomNavigationComponent(
    allScreens: List<Routes>,
    onTabSelected: (Routes) -> Unit,
    currentScreen: Routes
) {
    NavigationBar(
        tonalElevation = 16.dp,
    ) {
        allScreens.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = screen.selectedIcon,
                        contentDescription = screen.name
                    )
                },
                label = { screen.name },
                selected = currentScreen == screen,
                onClick = { onTabSelected(screen) }
            )
        }
    }
}
