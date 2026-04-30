package com.william.bizflow.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.william.bizflow.ui.screens.splash.SplashScreen
import com.william.bizflow.ui.screens.onbording.OnboardingScreen
import com.william.bizflow.ui.screens.auth.LoginScreen
import com.william.bizflow.ui.screens.dashboard.DashboardScreen
import com.william.bizflow.ui.screens.addproducts.AddProductScreen
import com.william.bizflow.ui.screens.auth.LoginScreen
import com.william.bizflow.ui.screens.customer.CustomerScreen
import com.william.bizflow.ui.screens.report.ReportScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash" // Start point
    ) {
        composable("splash") { SplashScreen(navController) }
        composable("onboarding") { OnboardingScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("dashboard") { DashboardScreen(navController) }
        composable("add_product") { AddProductScreen(navController) }
        composable("customer") { CustomerScreen(navController) }
        composable("report") { ReportScreen(navController) }
    }
}
