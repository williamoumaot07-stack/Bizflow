package com.william.bizflow.ui.screens.dashboardscreen

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
import com.william.bizflow.navigation.Routes

@Composable
fun DashboardScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(20.dp)
    ) {
        Text("Hello, Business Owner!", fontSize = 14.sp, color = Color.Gray)
        Text("Bizflow Summary", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth().height(150.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A73E8)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.Center) {
                Text("Today's Profit", color = Color.White.copy(alpha = 0.8f))
                Text("ksh850.00", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Row 1
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            DashboardButton("Inventory", Color(0xFF2196F3), Modifier.weight(1f)) {
                navController.navigate(Routes.VIEW_PRODUCTS)
            }
            DashboardButton("Customers", Color(0xFF4CAF50), Modifier.weight(1f)) {
                navController.navigate(Routes.CUSTOMER)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Row 2
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            DashboardButton("Reports", Color(0xFFFF9800), Modifier.weight(1f)) {
                navController.navigate(Routes.REPORT)
            }
            DashboardButton("Record Sale", Color(0xFFE91E63), Modifier.weight(1f)) {
                navController.navigate(Routes.ADD_SALE)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Row 3
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            DashboardButton("Add Stock", Color(0xFF9C27B0), Modifier.weight(1f)) {
                navController.navigate(Routes.ADD_PRODUCT)
            }
            DashboardButton("Sales History", Color(0xFF795548), Modifier.weight(1f)) {
                navController.navigate(Routes.VIEW_SALES)
            }
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