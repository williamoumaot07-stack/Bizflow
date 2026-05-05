package com.william.bizflow.ui.screens.sales

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.william.bizflow.data.SaleViewModel
import com.william.bizflow.models.Sale
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ViewSalesScreen(navController: NavController) {
    val context = LocalContext.current
    val saleViewModel = SaleViewModel(navController, context)
    val sales = remember { mutableStateListOf<Sale>() }

    LaunchedEffect(Unit) {
        saleViewModel.viewSales(sales)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Sales History", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(sales) { sale ->
                SaleItem(sale)
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
