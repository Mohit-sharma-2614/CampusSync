package com.example.campussync.persentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.campussync.navigation.Routes

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BottomNavigationComponent(
    allScreens: List<Routes>,
    onTabSelected: (Routes) -> Unit,
    currentScreen: Routes
) {
    NavigationBar(
        tonalElevation = 16.dp,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.surfaceContainerHighest
                    )
                ),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            )
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
    ) {
        allScreens.forEach { screen ->
            val isSelected = currentScreen == screen
            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.2f else 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy),
                label = "scale"
            )
            val iconColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                animationSpec = tween(durationMillis = 300),
                label = "iconColor"
            )
            val labelColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                animationSpec = tween(durationMillis = 300),
                label = "labelColor"
            )

            NavigationBarItem(
                icon = {
                    Box(
                        modifier = Modifier
                            .scale(scale)
                            .size(24.dp)
                    ) {
                        AnimatedContent(
                            targetState = isSelected,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(300)) with
                                        fadeOut(animationSpec = tween(300))
                            },
                            label = "iconTransition"
                        ) { selected ->
                            Icon(
                                imageVector = if (selected) screen.selectedIcon else screen.unselectedIcon,
                                contentDescription = screen.name,
                                tint = iconColor
                            )
                        }
//                        // Badge for notification count
//                        screen.badgeCount?.let { count ->
//                            if (count > 0) {
//                                Badge(
//                                    containerColor = MaterialTheme.colorScheme.error,
//                                    contentColor = MaterialTheme.colorScheme.onError,
//                                    modifier = Modifier
//                                        .align(LineHeightStyle.Alignment.TopEnd)
//                                        .offset(x = 6.dp, y = (-6).dp)
//                                        .scale(scale)
//                                ) {
//                                    Text(
//                                        text = count.toString(),
//                                        style = MaterialTheme.typography.labelSmall,
//                                        modifier = Modifier.padding(horizontal = 4.dp)
//                                    )
//                                }
//                            }
//                        }
                    }
                },
                label = {
                    Text(
                        text = screen.name,
                        style = MaterialTheme.typography.labelMedium,
                        color = labelColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                selected = isSelected,
                onClick = { onTabSelected(screen) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .then(
                        if (isSelected) Modifier.background(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        ) else Modifier
                    )
            )
        }
    }
}