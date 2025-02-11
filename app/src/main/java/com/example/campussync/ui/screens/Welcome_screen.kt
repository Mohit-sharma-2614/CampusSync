package com.example.campussync.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.campussync.R
import com.example.campussync.ui.components.CustomButton


@Composable
fun WelcomeScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 24.dp) // Moves the entire Column down
        ) {
            Image(
                painter = painterResource(R.drawable.bkbiet),
                contentDescription = null,
                modifier = Modifier.size(70.dp) // Sets fixed size directly
            )
            Spacer(modifier = Modifier.height(16.dp)) // Adds spacing between Image and Text
            Text(
                text = stringResource(R.string.welcome),
                color = colorResource(R.color.blue),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                style = TextStyle(fontStyle = FontStyle.Normal)
            )
            Spacer(modifier = Modifier.height(16.dp)) // Adds spacing between Texts
            Text(
                text = stringResource(R.string.description),
                color = colorResource(R.color.black),
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                style = TextStyle(fontStyle = FontStyle.Normal)
            )
            Spacer(modifier = Modifier.height(32.dp))
            CustomButton(
                onClick = { /* Handle login button click */ },
                text = stringResource(R.string.login),
                textColor = Color.Blue
            )
            Spacer(modifier = Modifier.height(16.dp))
            CustomButton(
                onClick = { /* Handle signup button click */ },
                text = stringResource(R.string.signup),
                textColor = Color.Blue
            )
            Spacer(modifier = Modifier.height(32.dp))
            Box( // Separate Box for last image
                modifier = Modifier.fillMaxWidth()
                    .fillMaxHeight(),
                contentAlignment = Alignment.BottomCenter // Centers the image
            ) {
                Image(
                    painter = painterResource(R.drawable.image_3),
                    contentDescription = null,
                    modifier = Modifier.size(300.dp)
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun WelcomeScreenPreview() {
    Scaffold { innerPadding ->
        WelcomeScreen(modifier = Modifier.padding(innerPadding).background(Color.White))
    }
}