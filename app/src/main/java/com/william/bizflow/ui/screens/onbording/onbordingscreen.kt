package com.william.bizflow.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun DashboardScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(20.dp)
    ) {
        // Simple Greeting
        Text("Hello, Business Owner!", fontSize = 14.sp, color = Color.Gray)
        Text("Bizflow Summary", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(20.dp))

        // Main Stat Card
        Card(
            modifier = Modifier.fillMaxWidth().height(150.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A73E8)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.Center) {
                Text("Today's Profit", color = Color.White.copy(alpha = 0.8f))
                Text("$850.00", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Row Buttons
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            DashboardButton("Record Sale", Color(0xFF4CAF50), Modifier.weight(1f)) { }
            DashboardButton("Add Product", Color(0xFF2196F3), Modifier.weight(1f)) { }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Secondary Row
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            DashboardButton("Expenses", Color(0xFFF44336), Modifier.weight(1f)) { }
            DashboardButton("Reports", Color(0xFFFF9800), Modifier.weight(1f)) { }
        }
    }
}

@Composable
fun DashboardButton(text: String, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier.height(80.dp).clickable { onClick() },
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 2.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text, color = color, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
@Preview(showBackground = true)
fun DashboardPreview() {
    DashboardScreen(rememberNavController())
}