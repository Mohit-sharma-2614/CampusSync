package com.example.campussync.utils.config

import androidx.compose.ui.graphics.vector.ImageVector

data class TopBarAction(
    val icon: ImageVector,
    val contentDescription: String,
    val onClick: () -> Unit
)
