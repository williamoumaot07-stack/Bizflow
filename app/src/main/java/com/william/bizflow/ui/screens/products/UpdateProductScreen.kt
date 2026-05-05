package com.william.bizflow.ui.screens.products

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.william.bizflow.data.ProductViewModel
import com.william.bizflow.models.Product

@Composable
fun UpdateProductScreen(navController: NavController, productId: String) {
    var productName by remember { mutableStateOf("") }
    var buyingPrice by remember { mutableStateOf("") }
    var sellingPrice by remember { mutableStateOf("") }
    var stockCount by remember { mutableStateOf("") }

    val context = LocalContext.current
    val productViewModel = ProductViewModel(navController, context)

    val nameState = remember { mutableStateOf("") }
    val bPriceState = remember { mutableStateOf("") }
    val sPriceState = remember { mutableStateOf("") }
    val stockState = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        productViewModel.fetchProduct(productId, nameState, bPriceState, sPriceState, stockState)
    }

    // Sync local state only once when data is fetched
    LaunchedEffect(nameState.value) { if(nameState.value.isNotEmpty()) productName = nameState.value }
    LaunchedEffect(bPriceState.value) { if(bPriceState.value.isNotEmpty()) buyingPrice = bPriceState.value }
    LaunchedEffect(sPriceState.value) { if(sPriceState.value.isNotEmpty()) sellingPrice = sPriceState.value }
    LaunchedEffect(stockState.value) { if(stockState.value.isNotEmpty()) stockCount = stockState.value }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text("Update Product", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(20.dp))

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
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = sellingPrice,
                onValueChange = { sellingPrice = it },
                label = { Text("Selling Price") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = stockCount,
            onValueChange = { stockCount = it },
            label = { Text("Stock Quantity") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                productViewModel.updateProduct(productName, buyingPrice, sellingPrice, stockCount, productId)
            },
            modifier = Modifier.fillMaxWidth().height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text("Update Product", fontSize = 18.sp)
        }
    }
}
