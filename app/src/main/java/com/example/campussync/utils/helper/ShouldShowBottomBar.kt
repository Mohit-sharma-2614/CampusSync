package com.example.campussync.utils.helper

import com.example.campussync.navigation.bottomNavItems

fun shouldShowBottomBar(route: String?): Boolean {
    return route in bottomNavItems.map { item ->
        item.route
    }
}