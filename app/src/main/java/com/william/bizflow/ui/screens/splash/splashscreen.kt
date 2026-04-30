package com.william.bizflow.ui.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    // Logic to navigate to Login after 2 seconds
    LaunchedEffect(key1 = true) {
        delay(2000L)
        // Replace "login_screen" with your actual route name
        navController.navigate("login_screen") {
            popUpTo("splash_screen") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A73E8)), // Professional Blue
        contentAlignment = Alignment.Center
    ) {
        // 1. Center Content: Logo and Name
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Replace with your actual logo resource
            /*
            Image(
                painter = painterResource(id = android.R.drawable.ic_menu_sort_by_size),
                contentDescription = "Logo",
                modifier = Modifier.size(100.dp)
            )
            */
            Text(
                text = "BIZFLOW",
                color = Color.White,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp
            )
            Text(
                text = "Manage smarter, grow faster",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
        }

        // 2. Bottom Content: Loading Indicator
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 50.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 2.dp,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun SplashPreview() {
    SplashScreen(rememberNavController())
}