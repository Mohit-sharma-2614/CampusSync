package com.example.campussync.utils.config

data class TopBarConfig(
    val title: String,
    val showBackButton: Boolean = false,
    val onBackClick: (() -> Unit)? = null,
    val actions: List<TopBarAction> = emptyList()
)

