package com.william.bizflow.ui.screens.customer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
fun CustomerScreen(navController: NavController) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Navigate to Add Customer */ },
                containerColor = Color(0xFF1A73E8)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
        ) {
            Text(
                text = "Customer Records",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Manage contacts and balances",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(20.dp))

            // The fixed LazyColumn
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Using the count-based items function
                items(5) { index ->
                    CustomerItem(
                        name = "Customer ${index + 1}",
                        phone = "+254 712 345 678",
                        balance = "$${(index + 1) * 50}.00"
                    )
                }
            }
        }
    }
}

@Composable
fun CustomerItem(name: String, phone: String, balance: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(phone, fontSize = 13.sp, color = Color.Gray)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Balance", fontSize = 10.sp, color = Color.Gray)
                Text(balance, fontWeight = FontWeight.Bold, color = Color(0xFFF44336))
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun CustomerPreview() {
    CustomerScreen(rememberNavController())
}