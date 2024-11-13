package com.example.newdemo.ComposablePackage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

@Composable
fun WelcomePage(username: String){
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Welcome $username!", fontSize = 24.sp, color = Color.Black)
    }
}

@Preview
@Composable
fun PreviewWelcomePage(){
    WelcomePage(username = "Sai")
}