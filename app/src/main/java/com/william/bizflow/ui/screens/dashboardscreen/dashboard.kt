package com.william.bizflow.ui.screens.dashboardscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import android.content.Intent
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import androidx.compose.ui.res.painterResource
import com.william.bizflow.R
import com.william.bizflow.data.ProductViewModel
import com.william.bizflow.data.SaleViewModel
import com.william.bizflow.models.Product
import com.william.bizflow.models.Sale
import com.william.bizflow.models.User
import com.william.bizflow.navigation.Routes
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    val context = LocalContext.current
    val saleViewModel = SaleViewModel(navController, context)
    val productViewModel = ProductViewModel(navController, context)
    val sales = remember { mutableStateListOf<Sale>() }
    val products = remember { mutableStateListOf<Product>() }
    val productState = remember { mutableStateOf(Product()) }
    var userName by remember { mutableStateOf("Business Owner") }

    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid
    var profileImageUrl by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        saleViewModel.viewSales(sales)
        productViewModel.viewProducts(productState, products)
    }

    DisposableEffect(userId) {
        if (userId != null) {
            val userRef = FirebaseDatabase.getInstance().getReference("Users/$userId")
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null) {
                        userName = user.name
                        profileImageUrl = user.profileImageUrl
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            }
            userRef.addValueEventListener(listener)
            onDispose { userRef.removeEventListener(listener) }
        } else {
            onDispose {}
        }
    }

    // Calculate Today's Profit (using derivedStateOf for better performance with SnapshotStateList)
    val todayProfit by remember {
        derivedStateOf {
            val todayDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
            sales.filter {
                val saleDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date(it.timestamp))
                saleDate == todayDate
            }.sumOf { it.profit.toDoubleOrNull() ?: 0.0 }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { 
                        if (!navController.popBackStack()) {
                            navController.navigate(Routes.HOME)
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, "Check out my business stats on BizFlow! Today's Profit: Ksh ${String.format("%.2f", todayProfit)}")
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)
                        context.startActivity(shareIntent)
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1A237E))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hello, $userName!",
                        fontSize = 16.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Bizflow Summary",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1A237E)
                    )
                }
                
                Box(
                    modifier = Modifier
                        .size(55.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8F0FE))
                        .clickable { navController.navigate(Routes.PROFILE) },
                    contentAlignment = Alignment.Center
                ) {
                    if (profileImageUrl.isNotEmpty()) {
                        AsyncImage(
                            model = profileImageUrl,
                            contentDescription = "Profile",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            painter = painterResource(id = R.drawable.bz), // Fallback to logo
                            contentDescription = "Profile",
                            modifier = Modifier.size(30.dp),
                            tint = Color(0xFF1A237E)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Today's Profit Card - High Visibility
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A237E)),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Decorative background element
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 20.dp, y = (-20).dp)
                            .size(120.dp)
                            .background(Color.White.copy(alpha = 0.1f), CircleShape)
                    )
                    
                    Column(
                        Modifier
                            .padding(24.dp)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "Today's Profit",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Ksh ",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                String.format(Locale.getDefault(), "%.2f", todayProfit),
                                color = Color.White,
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    label = "Products",
                    value = products.size.toString(),
                    color = Color(0xFF1A237E),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "Total Sales",
                    value = sales.size.toString(),
                    color = Color(0xFF1A237E),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Low Stock Alert Section
            val lowStockProducts = products.filter { (it.stockCount.toIntOrNull() ?: 0) < 5 }
            if (lowStockProducts.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Low Stock",
                                tint = Color.Red
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Low Stock Alert!",
                                color = Color.Red,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        lowStockProducts.take(3).forEach { product ->
                            Text(
                                "• ${product.name} is low on stock (${product.stockCount} left)",
                                color = Color.Black,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }
                        if (lowStockProducts.size > 3) {
                            Text(
                                "...and ${lowStockProducts.size - 3} more",
                                color = Color.Black,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Row 1
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                DashboardButton("Inventory", Color(0xFF1A237E), Modifier.weight(1f)) {
                    navController.navigate(Routes.VIEW_PRODUCTS)
                }
                DashboardButton("Customers", Color(0xFF1A237E), Modifier.weight(1f)) {
                    navController.navigate(Routes.CUSTOMER)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Row 2
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                DashboardButton("Reports", Color(0xFF1A237E), Modifier.weight(1f)) {
                    navController.navigate(Routes.REPORT)
                }
                DashboardButton("Record Sale", Color(0xFF1A237E), Modifier.weight(1f)) {
                    navController.navigate(Routes.ADD_SALE)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Row 3
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                DashboardButton("Add Stock", Color(0xFF1A237E), Modifier.weight(1f)) {
                    navController.navigate(Routes.ADD_PRODUCT)
                }
                DashboardButton("Sales History", Color(0xFF1A237E), Modifier.weight(1f)) {
                    navController.navigate(Routes.VIEW_SALES)
                }
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.height(100.dp),
        color = Color.White,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(label, color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
            Text(value, color = color, fontSize = 28.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
fun DashboardButton(text: String, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier.height(80.dp).clickable { onClick() },
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 2.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text, color = color, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
        }
    }
}

@Composable
@Preview(showBackground = true)
fun DashboardPreview() {
    DashboardScreen(rememberNavController())
}