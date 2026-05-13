package com.william.bizflow.ui.screens.sales

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.*
import com.william.bizflow.utils.ExportUtils
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.william.bizflow.data.SaleViewModel
import com.william.bizflow.models.Sale
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewSalesScreen(navController: NavController) {
    val context = LocalContext.current
    val saleViewModel = SaleViewModel(navController, context)
    val sales = remember { mutableStateListOf<Sale>() }
    var searchQuery by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        saleViewModel.viewSales(sales)
    }

    val filteredSales = if (searchQuery.isEmpty()) {
        sales
    } else {
        sales.filter { it.productName.contains(searchQuery, ignoreCase = true) }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Clear Sales History", fontWeight = FontWeight.Black) },
            text = { Text("Are you sure you want to delete all sales records? This action cannot be undone.", fontWeight = FontWeight.Bold) },
            confirmButton = {
                Button(
                    onClick = {
                        saleViewModel.clearSalesHistory()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text("Clear All", color = Color.White, fontWeight = FontWeight.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = Color(0xFF1A237E), fontWeight = FontWeight.Black)
                }
            },
            containerColor = Color.White
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sales History", color = Color.White, fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    if (sales.isNotEmpty()) {
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(Icons.Default.Share, contentDescription = "Export", tint = Color.White)
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Export as CSV") },
                                    onClick = {
                                        showMenu = false
                                        ExportUtils.exportSalesToCSV(context, sales)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Export as PDF") },
                                    onClick = {
                                        showMenu = false
                                        ExportUtils.exportSalesToPDF(context, sales)
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1A237E))
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            if (sales.isNotEmpty()) {
                // Sales Trend Chart
                Text("Sales Trend (Last 7 Sales)", fontWeight = FontWeight.Black, color = Color.Black, modifier = Modifier.padding(bottom = 8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().height(120.dp).padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    SalesChart(sales.takeLast(7))
                }

                // Sales Summary Dashboard
                val totalSales = sales.sumOf { it.totalAmount.toDoubleOrNull() ?: 0.0 }
                val totalProfit = sales.sumOf { it.profit.toDoubleOrNull() ?: 0.0 }

                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Sales", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Text("Ksh $totalSales", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFF1A237E))
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Total Profit", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Text("Ksh $totalProfit", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFF2E7D32))
                        }
                    }
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search sales...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedLabelColor = Color(0xFF1A237E),
                        unfocusedLabelColor = Color.DarkGray,
                        focusedContainerColor = Color(0xFFF8F9FA),
                        unfocusedContainerColor = Color(0xFFF8F9FA),
                        cursorColor = Color(0xFF1A237E)
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { showDeleteDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Clear All History", color = Color.White, fontWeight = FontWeight.Black)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (filteredSales.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(if (searchQuery.isEmpty()) "No sales records found" else "No sales match your search", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            } else {
                LazyColumn {
                    items(filteredSales) { sale ->
                        SaleItem(sale)
                    }
                }
            }
        }
    }
}

@Composable
fun SalesChart(recentSales: List<Sale>) {
    val maxSale = recentSales.maxOfOrNull { it.totalAmount.toDoubleOrNull() ?: 1.0 } ?: 1.0
    Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        val width = size.width
        val height = size.height
        if (recentSales.isEmpty()) return@Canvas
        val barWidth = width / (recentSales.size * 2f)

        recentSales.forEachIndexed { index, sale ->
            val saleVal = sale.totalAmount.toDoubleOrNull() ?: 0.0
            val barHeight = (saleVal / maxSale) * height
            drawRect(
                color = Color(0xFF1A237E),
                topLeft = Offset(x = index * barWidth * 2f + barWidth / 2f, y = (height - barHeight).toFloat()),
                size = Size(barWidth, barHeight.toFloat())
            )
        }
    }
}

@Composable
fun SaleItem(sale: Sale) {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val date = sdf.format(Date(sale.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = sale.productName, fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color.Black)
                Text(text = date, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = Color.Black)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Quantity: ${sale.quantity}", fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = "Total Amount: Ksh ${sale.totalAmount}", fontWeight = FontWeight.Bold, color = Color(0xFF1A237E))
            Text(text = "Profit: Ksh ${sale.profit}", fontWeight = FontWeight.Black, color = Color(0xFF2E7D32))
        }
    }
}
