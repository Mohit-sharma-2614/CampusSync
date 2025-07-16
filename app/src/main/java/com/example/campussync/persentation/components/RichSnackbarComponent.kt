package com.example.campussync.persentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RichSnackbarComponent(
    modifier: Modifier = Modifier,
    message: String,
    isError: Boolean = false,
    onActionClick: (() -> Unit)? = null,
    actionLabel: String = "Retry",
    duration: SnackbarDuration = SnackbarDuration.Short
) {
    val snackbarHostState = remember { SnackbarHostState() }

    // Automatically show snackbar when called
    LaunchedEffect(message) {
        snackbarHostState.showSnackbar(
            message = message,
            actionLabel = if (onActionClick != null) actionLabel else null,
            duration = duration
        )
    }

    SnackbarHost(
        hostState = snackbarHostState,
        modifier = modifier.fillMaxWidth(),
        snackbar = { snackbarData ->
            Surface(
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 6.dp,
                shadowElevation = 12.dp,
                color = if (isError) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    // 🟢 Icon
                    Icon(
                        imageVector = if (isError) Icons.Default.Error else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (isError) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    // 📢 Message
                    Text(
                        text = snackbarData.visuals.message,
                        color = if (isError) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 16.sp,
                        modifier = Modifier.weight(1f)
                    )

                    // ✋ Action (optional)
                    snackbarData.visuals.actionLabel?.let {
                        TextButton(onClick = { onActionClick?.invoke() }) {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    )
}