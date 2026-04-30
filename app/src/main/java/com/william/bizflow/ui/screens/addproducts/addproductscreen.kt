package com.william.bizflow.ui.screens.addproducts

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun AddProductScreen(navController: NavController) {
    var productName by remember { mutableStateOf("") }
    var buyingPrice by remember { mutableStateOf("") }
    var sellingPrice by remember { mutableStateOf("") }
    var stockCount by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text("Add New Product", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))

        // Input Fields
        OutlinedTextField(
            value = productName,
            onValueChange = { productName = it },
            label = { Text("Product Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedTextField(
                value = buyingPrice,
                onValueChange = { buyingPrice = it },
                label = { Text("Buying Price") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = sellingPrice,
                onValueChange = { sellingPrice = it },
                label = { Text("Selling Price") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = stockCount,
            onValueChange = { stockCount = it },
            label = { Text("Initial Stock Quantity") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Save Button
        Button(
            onClick = { /* Save logic here */ },
            modifier = Modifier.fillMaxWidth().height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A73E8))
        ) {
            Text("Save Product", fontSize = 18.sp)
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AddProductPreview() {
    AddProductScreen(rememberNavController())
}