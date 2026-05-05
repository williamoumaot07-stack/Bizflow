package com.william.bizflow.ui.screens.sales

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.william.bizflow.data.ProductViewModel
import com.william.bizflow.data.SaleViewModel
import com.william.bizflow.models.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSaleScreen(navController: NavController) {
    val context = LocalContext.current
    val productViewModel = ProductViewModel(navController, context)
    val saleViewModel = SaleViewModel(navController, context)

    val products = remember { mutableStateListOf<Product>() }
    val product = remember { mutableStateOf(Product()) }

    productViewModel.viewProducts(product, products)

    var quantity by remember { mutableStateOf("") }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "New Sale", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedProduct == null) {
            Text(text = "Select a product from the list below")
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn {
                items(products) { prod ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        onClick = { selectedProduct = prod }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = prod.name, style = MaterialTheme.typography.titleLarge)
                            Text(text = "Stock: ${prod.stockCount}")
                            Text(text = "Price: ${prod.sellingPrice}")
                        }
                    }
                }
            }
        } else {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Selected Product: ${selectedProduct!!.name}")
                    Text(text = "Current Stock: ${selectedProduct!!.stockCount}")
                    Text(text = "Unit Price: ${selectedProduct!!.sellingPrice}")

                    OutlinedButton(onClick = { selectedProduct = null }) {
                        Text(text = "Change Product")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = quantity,
                onValueChange = { quantity = it },
                label = { Text("Quantity") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    saleViewModel.recordSale(selectedProduct!!, quantity)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Record Sale")
            }
        }
    }
}
