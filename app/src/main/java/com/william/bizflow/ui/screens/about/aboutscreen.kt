package com.william.bizflow.ui.screens.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About Bizflow", color = Color.White, fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
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
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // App Logo
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .background(Color(0xFF1A237E), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("BZ", fontSize = 42.sp, fontWeight = FontWeight.Black, color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("BizFlow", fontSize = 36.sp, fontWeight = FontWeight.Black, color = Color(0xFF1A237E))
            Text("Version 1.0.0", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Your simple business management companion",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.DarkGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // What is BizFlow
            AboutCard(
                title = "📱 What is BizFlow?",
                content = "BizFlow is a simple and powerful business management app designed for small business owners. " +
                        "It helps you track your products, manage stock levels, record sales, and monitor your daily profits " +
                        "— all from your phone."
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Features
            AboutCard(
                title = "✨ Key Features",
                content = """
- 🛒 Add & manage products with buying and selling prices
- 📦 Track stock levels in real time
- 💰 Monitor today's profit at a glance
- 📊 View detailed sales reports
- 👥 Manage your customers
- 🔒 Simple and secure login system
                """.trimIndent()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // How to use
            AboutCard(
                title = "📖 How to Use",
                content = """
1. LOGIN — Sign in with your business account
2. DASHBOARD — View your daily profit summary
3. ADD PRODUCT — Enter product name, buying price, selling price and stock
4. CUSTOMERS — Add and manage your customers
5. REPORTS — View your sales and profit history
6. SAVE — All your data is saved automatically
                """.trimIndent()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tips
            AboutCard(
                title = "💡 Tips for Best Use",
                content = """
- Always update stock after receiving new goods
- Record every sale to keep profits accurate
- Check your dashboard daily to track performance
- Use the reports section at end of each week
- Keep your buying price updated when costs change
                """.trimIndent()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Developer info
            AboutCard(
                title = "👨‍💻 Developer",
                content = """
App Name: BizFlow
Developer: William
Platform: Android
Built with: Kotlin & Jetpack Compose
Database: Firebase (Realtime Database)
Purpose: Small Business Management
                """.trimIndent()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Contact
            AboutCard(
                title = "📞 Support & Contact",
                content = """"
If you experience any issues or have suggestions to improve BizFlow, please reach out:

- Email: williamoumaot07@gmail.com
- The app is regularly updated with new features
- Your feedback helps us improve!
- Phone number :0720207377
                """.trimIndent()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Back Button
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A237E)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Back to Dashboard", fontSize = 18.sp, fontWeight = FontWeight.Black)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "© 2026 BizFlow. All rights reserved.",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun AboutCard(title: String, content: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F0FE)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF1A237E)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = content,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                lineHeight = 24.sp
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AboutPreview() {
    AboutScreen(rememberNavController())
}
