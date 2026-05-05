package com.william.bizflow.ui.screens.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

@Composable
fun ReportScreen(navController: NavController) {
    val context = LocalContext.current
    val productViewModel = ProductViewModel(navController, context)
    val saleViewModel = SaleViewModel(navController, context)
    val emptyProductState = remember { mutableStateOf(Product("", "", "", "", "")) }
    val emptyProductsListState = remember { mutableStateListOf<Product>() }
    val sales = remember { mutableStateListOf<Sale>() }

    val products = productViewModel.viewProducts(emptyProductState, emptyProductsListState)
    saleViewModel.viewSales(sales)

    // Basic calculation for analytics
    val totalStockValue = products.sumOf { (it.buyingPrice.toDoubleOrNull() ?: 0.0) * (it.stockCount.toDoubleOrNull() ?: 0.0) }
    val potentialProfit = products.sumOf { 
        ((it.sellingPrice.toDoubleOrNull() ?: 0.0) - (it.buyingPrice.toDoubleOrNull() ?: 0.0)) * (it.stockCount.toDoubleOrNull() ?: 0.0)
    }
    val totalProducts = products.size

    val actualRevenue = sales.sumOf { it.totalAmount.toDoubleOrNull() ?: 0.0 }
    val actualProfit = sales.sumOf { it.profit.toDoubleOrNull() ?: 0.0 }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(20.dp)
    ) {
        Text("Business Insights", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Real-time inventory analytics", fontSize = 14.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(24.dp))

        // Financial Summary Cards
        ReportMetricCard("Total Sales Revenue", "ksh $actualRevenue", Color(0xFF1A73E8))
        Spacer(modifier = Modifier.height(12.dp))
        ReportMetricCard("Actual Profit", "ksh $actualProfit", Color(0xFF4CAF50))
        Spacer(modifier = Modifier.height(12.dp))
        ReportMetricCard("Inventory Items", totalProducts.toString(), Color(0xFF9C27B0))
        Spacer(modifier = Modifier.height(12.dp))
        ReportMetricCard("Total Stock Value", "ksh $totalStockValue", Color(0xFFFF9800))
        Spacer(modifier = Modifier.height(12.dp))
        ReportMetricCard("Potential Remaining Profit", "ksh $potentialProfit", Color(0xFF795548))

        Spacer(modifier = Modifier.height(30.dp))

        Text("Profit Margin Strategy", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            modifier = Modifier.fillMaxWidth().height(150.dp),
            color = Color.White,
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 2.dp
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (totalProducts > 0 || sales.isNotEmpty()) 
                        "Actual Profit earned so far is ksh $actualProfit from ksh $actualRevenue revenue. Remaining potential inventory profit is ksh $potentialProfit." 
                           else "Add products and record sales to see business analytics.",
                    color = Color.DarkGray,
                    fontSize = 14.sp
                )
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