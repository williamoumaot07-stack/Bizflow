package com.william.bizflow.ui.screens.products

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import android.content.Intent
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.william.bizflow.data.ProductViewModel
import com.william.bizflow.models.Product
import com.william.bizflow.navigation.Routes
import com.william.bizflow.utils.ExportUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(navController: NavController) {
    val context = LocalContext.current
    val productViewModel = ProductViewModel(navController, context)
    val emptyProductState = remember { mutableStateOf(Product("", "", "", "", "")) }
    val emptyProductsListState = remember { mutableStateListOf<Product>() }

    val products = productViewModel.viewProducts(emptyProductState, emptyProductsListState)
    var searchQuery by remember { mutableStateOf("") }
    var showMenu by remember { mutableStateOf(false) }

    val filteredProducts = products.filter { 
        it.name.contains(searchQuery, ignoreCase = true) 
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventory", color = Color.White, fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.Share, contentDescription = "Export", tint = Color.White)
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Share via Text") },
                                onClick = {
                                    showMenu = false
                                    val inventoryList = products.joinToString("\n") { "${it.name}: Ksh ${it.sellingPrice} (${it.stockCount} left)" }
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, "BizFlow Inventory:\n\n$inventoryList")
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, null))
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Export as CSV") },
                                onClick = {
                                    showMenu = false
                                    ExportUtils.exportProductsToCSV(context, products)
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1A237E))
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Routes.ADD_PRODUCT) },
                containerColor = Color(0xFF1A237E)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Inventory Value Dashboard
            if (products.isNotEmpty()) {
                val totalValue = products.sumOf { 
                    (it.sellingPrice.toDoubleOrNull() ?: 0.0) * (it.stockCount.toDoubleOrNull() ?: 0.0) 
                }
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Total Inventory Value", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text("Ksh $totalValue", fontSize = 24.sp, fontWeight = FontWeight.Black, color = Color(0xFF1A237E))
                        Text("Potential revenue from current stock", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search products...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF1A237E),
                    focusedLabelColor = Color(0xFF1A237E),
                    unfocusedBorderColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredProducts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        if (searchQuery.isEmpty()) "Your inventory is empty" else "No matching products",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(filteredProducts) { product ->
                        ProductItem(
                            product = product,
                            navController = navController,
                            onDelete = { productViewModel.deleteProduct(product.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductItem(product: Product, navController: NavController, onDelete: () -> Unit) {
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
                Text(product.name, fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color.Black)
                val stockInt = product.stockCount.toDoubleOrNull() ?: 0.0
                Text(
                    text = if (stockInt < 5) "Low Stock: ${product.stockCount}" else "In Stock: ${product.stockCount}",
                    fontSize = 15.sp,
                    color = if (stockInt < 5) Color.Red else Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("ksh ${product.sellingPrice}", fontWeight = FontWeight.Black, color = Color(0xFF2E7D32), fontSize = 16.sp)
                Row {
                    IconButton(onClick = { navController.navigate(Routes.UPDATE_PRODUCT + "/${product.id}") }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Gray)
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                    }
                }
            }
        }
    }
}
