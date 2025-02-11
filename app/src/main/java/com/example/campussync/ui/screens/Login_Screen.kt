package com.example.campussync.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.campussync.R
import com.example.campussync.ui.components.CustomButton
import com.example.campussync.ui.components.CustomTextField
import com.example.campussync.ui.utils.SimpleIconButton

@Composable
fun Login_Screen(
    modifier: Modifier = Modifier
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 24.dp)
            .background(
                color = Color.White,
                shape = RectangleShape
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.bkbiet),
            contentDescription = null,
            modifier = Modifier.size(70.dp)
        )

        Text(
            text = stringResource(R.string.welcome),
            color = colorResource(R.color.blue),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = stringResource(R.string.description),
            color = colorResource(R.color.black),
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal
        )

        CustomTextField(
            value = username,
            onValueChange = { username = it },
            labelText = "UserName"
        )

        CustomTextField(
            value = password,
            onValueChange = { password = it },
            labelText = "Password",
            keyboardType = KeyboardType.Password,
            visualTransformation = PasswordVisualTransformation()
        )

        TextButton(
            onClick = { /* Handle forgot password click */ },
            colors = ButtonDefaults.textButtonColors(
                contentColor = colorResource(R.color.blue)
            )
        ) {
            Text(
                text = stringResource(R.string.forgot_password),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        SimpleIconButton(
            onClick = { /* Handle Google login click */ },
            painter = R.drawable.google,
            contentDescription = "Google Icon",
            size = 30
        )

        CustomButton(
            onClick = { /* Handle login button click */ },
            text = stringResource(R.string.login),
            textColor = Color.Blue
        )
        Image(
            painter = painterResource(R.drawable.image_1),
            contentDescription = null,
            modifier = Modifier.size(300.dp)
        )
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun Login_ScreenPreview() {
    Scaffold { innerPadding ->
        Login_Screen(modifier = Modifier.padding(innerPadding))
    }
}