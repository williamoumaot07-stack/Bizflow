package com.william.bizflow.ui.screens.sales

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import com.william.bizflow.utils.ExportUtils
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        saleViewModel.viewSales(sales)
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Clear Sales History") },
            text = { Text("Are you sure you want to delete all sales records? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    saleViewModel.clearSalesHistory()
                    showDeleteDialog = false
                }) {
                    Text("Clear All", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sales History", color = Color.White, fontWeight = FontWeight.Bold) },
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1A73E8))
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            if (sales.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { showDeleteDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Clear All History", color = Color.White)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (sales.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No sales records found", color = Color.Gray)
                }
            } else {
                LazyColumn {
                    items(sales) { sale ->
                        SaleItem(sale)
                    }
                }
            }
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
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = sale.productName, style = MaterialTheme.typography.titleLarge)
                Text(text = date, style = MaterialTheme.typography.bodySmall)
            }
            Text(text = "Quantity: ${sale.quantity}")
            Text(text = "Total Amount: ${sale.totalAmount}")
            Text(text = "Profit: ${sale.profit}")
        }
    }
}
