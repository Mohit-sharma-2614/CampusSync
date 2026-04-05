package com.example.campussync.persentation.qr

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.campussync.utils.config.TopBarConfig

@Composable
fun QrScreen(
    setTopBar: (TopBarConfig) -> Unit
){
    LaunchedEffect(Unit) {
        setTopBar(
            TopBarConfig(
                title = "QR Screen"
            )
        )
    }
    QrScreenContent ()
}

@Composable
fun QrScreenContent(){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "QR Screen",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            softWrap = true
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "This is the QR screen content.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            softWrap = true
        )
    }
}