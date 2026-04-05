package com.example.campussync.utils

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.campussync.navigation.Routes

@Composable
fun AppBottomBar(
    items: List<Routes>,
    currentRoute: String?,
    onItemClick: (Routes) -> Unit
    ){
    NavigationBar {
        items.forEach { item ->

            val isSelected = currentRoute == item.route

            NavigationBarItem(
                selected = isSelected,
                onClick = { onItemClick(item) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) {
                            item.selectedIcon
                        } else {
                            item.unselectedIcon
                        },
                        contentDescription = item.name
                    )
                },
                label = {
                    Text(item.name)
                }
            )
        }
    }
}