package com.william.bizflow.ui.screens.report

import androidx.compose.foundation.background
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
fun ReportScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(20.dp)
    ) {
        Text("Business Reports", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Monthly performance summary", fontSize = 14.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(24.dp))

        // Financial Summary Cards
        ReportMetricCard("Total Sales", "$5,000.00", Color(0xFF4CAF50))
        Spacer(modifier = Modifier.height(12.dp))
        ReportMetricCard("Total Expenses", "$1,200.00", Color(0xFFF44336))
        Spacer(modifier = Modifier.height(12.dp))
        ReportMetricCard("Net Profit", "$3,800.00", Color(0xFF1A73E8))

        Spacer(modifier = Modifier.height(30.dp))

        // Visual Placeholder for a Chart
        Text("Sales Trend", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            modifier = Modifier.fillMaxWidth().height(200.dp),
            color = Color.White,
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 2.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("Chart Visualization Here", color = Color.LightGray)
                // Later, you can integrate MPAndroidChart or Compose Charts here
            }
        }
    }
}

@Composable
fun ReportMetricCard(label: String, amount: String, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, fontWeight = FontWeight.Medium, color = Color.DarkGray)
            Text(amount, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = color)
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ReportPreview() {
    ReportScreen(rememberNavController())
}