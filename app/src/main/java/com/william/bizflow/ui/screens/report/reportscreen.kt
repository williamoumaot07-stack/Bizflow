package com.william.bizflow.ui.screens.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.william.bizflow.data.ProductViewModel
import com.william.bizflow.data.SaleViewModel
import com.william.bizflow.models.Product
import com.william.bizflow.models.Sale

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import com.william.bizflow.utils.ExportUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(navController: NavController) {
    val context = LocalContext.current
    val productViewModel = ProductViewModel(navController, context)
    val saleViewModel = SaleViewModel(navController, context)
    val emptyProductState = remember { mutableStateOf(Product("", "", "", "", "")) }
    val emptyProductsListState = remember { mutableStateListOf<Product>() }
    val sales = remember { mutableStateListOf<Sale>() }

    LaunchedEffect(Unit) {
        productViewModel.viewProducts(emptyProductState, emptyProductsListState)
        saleViewModel.viewSales(sales)
    }

    val products = emptyProductsListState

    // Basic calculation for analytics
    val totalStockValue = products.sumOf { (it.buyingPrice.toDoubleOrNull() ?: 0.0) * (it.stockCount.toDoubleOrNull() ?: 0.0) }
    val potentialProfit = products.sumOf { 
        ((it.sellingPrice.toDoubleOrNull() ?: 0.0) - (it.buyingPrice.toDoubleOrNull() ?: 0.0)) * (it.stockCount.toDoubleOrNull() ?: 0.0)
    }
    val totalProducts = products.size

    val actualRevenue = sales.sumOf { it.totalAmount.toDoubleOrNull() ?: 0.0 }
    val actualProfit = sales.sumOf { it.profit.toDoubleOrNull() ?: 0.0 }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Business Insights", color = Color.White, fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        ExportUtils.exportFinancialSummaryToPDF(
                            context, actualRevenue, actualProfit, totalProducts, totalStockValue, potentialProfit
                        )
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Export Report", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1A237E))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(padding)
                .padding(20.dp)
        ) {
            Text("Analytics Summary", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color(0xFF1A237E))
            Text("Real-time business tracking", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Black)

            Spacer(modifier = Modifier.height(24.dp))

            // Financial Summary Cards
            ReportMetricCard("Total Sales Revenue", "ksh $actualRevenue", Color(0xFF1A237E))
            Spacer(modifier = Modifier.height(12.dp))
            ReportMetricCard("Actual Profit", "ksh $actualProfit", Color(0xFF2E7D32))
            Spacer(modifier = Modifier.height(12.dp))
            ReportMetricCard("Inventory Items", totalProducts.toString(), Color(0xFF9C27B0))
            Spacer(modifier = Modifier.height(12.dp))
            ReportMetricCard("Total Stock Value", "ksh $totalStockValue", Color(0xFFE65100))
            Spacer(modifier = Modifier.height(12.dp))
            ReportMetricCard("Potential Remaining Profit", "ksh $potentialProfit", Color(0xFF5D4037))

            Spacer(modifier = Modifier.height(30.dp))

            Text("Profit Margin Strategy", fontWeight = FontWeight.Black, fontSize = 22.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = if (totalProducts > 0 || sales.isNotEmpty()) 
                            "Actual Profit earned so far is ksh $actualProfit from ksh $actualRevenue revenue. Remaining potential inventory profit is ksh $potentialProfit." 
                               else "Add products and record sales to see business analytics.",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun ReportMetricCard(label: String, amount: String, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, fontWeight = FontWeight.ExtraBold, color = Color.Black, fontSize = 16.sp)
            Text(amount, fontWeight = FontWeight.Black, fontSize = 20.sp, color = color)
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ReportPreview() {
    ReportScreen(rememberNavController())
}