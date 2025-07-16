package com.example.campussync.persentation.classes

import android.graphics.ColorSpace
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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.VectorConverter
import androidx.compose.animation.core.AnimationVector4D
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class) // For AnimatedContent
@Composable
fun ClassesScreen(
    onOkClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Classes Screen",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding->
        Box(
            modifier = modifier
                .padding(innerPadding)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
            propagateMinConstraints = false,
        ) {
            Surface(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                color = Color.Transparent
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // --- Animated Text Examples ---

                    // 1. Fade-in and Slide-in from Left
//                var showAnimatedText by remember { mutableStateOf(false) }
//                LaunchedEffect(Unit) {
//                    delay(300) // Small delay to let the screen settle
//                    showAnimatedText = true
//                }
//                AnimatedVisibility(
//                    visible = showAnimatedText,
//                    enter = slideInHorizontally(initialOffsetX = { -it / 2 }) + fadeIn(), // Slides from left
//                    exit = slideOutHorizontally(targetOffsetX = { -it / 2 }) + fadeOut()
//                ) {
//                    Text(
//                        text = "This is the Classes Screen. You can view and manage your classes here.",
//                        fontSize = 16.sp,
//                        color = Color.White,
//                        modifier = Modifier.padding(bottom = 16.dp)
//                    )
//                }

                    // 2. Typewriter Effect (replace the above if you want only this)
                    // If you want to use this, comment out the AnimatedVisibility block above
                    /*
                    var typedText by remember { mutableStateOf("") }
                    val fullText = "This is the Classes Screen. You can view and manage your classes here."
                    LaunchedEffect(fullText) {
                        for (i in fullText.indices) {
                            typedText = fullText.substring(0, i + 1)
                            delay(50) // Adjust typing speed here
                        }
                    }
                    Text(
                        text = typedText,
                        fontSize = 16.sp,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    */

                    // 3. Color Animation (can be combined with other effects on a single Text)
                    /* val infiniteTransition = rememberInfiniteTransition()

                     val animatedColor by infiniteTransition.animateValue(
                         initialValue = Color.White,
                         targetValue = Color(0xFFFFA07A),
                         animationSpec = infiniteRepeatable(
                             animation = tween(durationMillis = 2000),
                             repeatMode = RepeatMode.Reverse
                         ),
                         label = "animated_color",
                         typeConverter = TwoWayConverter<Color, AnimationVector4D>(
                             convertToVector = { color ->
                                 AnimationVector4D(
                                     color.red,
                                     color.green,
                                     color.blue,
                                     color.alpha
                                 )
                             },
                             convertFromVector = { vector ->
                                 Color(vector.v1, vector.v2, vector.v3, vector.v4)
                             }
                         )
                     )
                     Text(
                         text = "This is the Classes Screen. You can view and manage your classes here.",
                         fontSize = 16.sp,
                         color = animatedColor, // Use the animated color here
                         modifier = Modifier.padding(bottom = 16.dp)
                     )*/

                    // 4. Infinite Pulse Scale (can be combined with other effects on a single Text)
                    val infiniteTransition = rememberInfiniteTransition(label = "pulse_scale")
                    val scale by infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 1.05f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(durationMillis = 1000),
                            repeatMode = RepeatMode.Reverse
                        ), label = "pulse_scale_animation"
                    )
                    Text(
                        text = "This is the Classes Screen. You can view and manage your classes here.",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .scale(scale) // Apply the animated scale here
                    )

                    // --- End Animated Text Examples ---

                    Button(
                        onClick = onOkClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF)),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(6.dp)
                    ) {
                        Text(text = "Ok", color = Color.White)
                    }
                }
            }
        }
    }
}