package com.example.campussync.ui.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.campussync.R

@Composable
fun SimpleIconButton(
    onClick: () -> Unit,
    painter: Int,
    contentDescription: String,
    size: Int,
    modifier: Modifier = Modifier
){
    IconButton(
        onClick = onClick,
        modifier = Modifier.height(50.dp)
    ) {
        Image(
            painter = painterResource(painter),
            contentDescription = contentDescription,
            modifier = Modifier.size(size.dp)
        )
    }
}

@Composable
fun SimpleTextButton(
    onClick: () -> Unit,
    contentColor: Int,
    text: Int,
    fontSize: Int,
    modifier: Modifier = Modifier

){
    TextButton(
        modifier = modifier,
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(
            contentColor = colorResource(contentColor)
        )
    ) {
        Text(
            text = stringResource(text),
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}
