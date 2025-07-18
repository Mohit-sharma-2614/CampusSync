package com.example.campussync.persentation.assignment

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.campussync.persentation.components.LogoutConfirmationDialog

@Composable
fun AssignmentScreen(
    onOkClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AssignmentViewModel = hiltViewModel()
){
    AssignmentDialog(
        onDismiss = { onOkClick() },
        onConfirm = { onOkClick() }
    )
}

@Composable
fun AssignmentDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
){
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF1E1E2C), // Dark background
            tonalElevation = 8.dp,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .animateContentSize()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ✨ Fancy Emoji Animation
                Text(
                    text = "🚪",
                    fontSize = 48.sp,
                    modifier = Modifier
                        .scale(1.2f)
                        .graphicsLayer {
                            shadowElevation = 12f
                        }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Assignment Screen.",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "To be implemented.",
                    textAlign = TextAlign.Center,
                    color = Color.LightGray,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE63946)),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(6.dp)
                    ) {
                        Text(text = "Ok!", color = Color.White)
                    }
                }
            }
        }
    }
}