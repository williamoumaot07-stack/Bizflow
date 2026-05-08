package com.william.bizflow.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.william.bizflow.ui.screens.about.AboutScreen
import com.william.bizflow.ui.screens.addproducts.AddProductScreen
import com.william.bizflow.ui.screens.auth.LoginScreen
import com.william.bizflow.ui.screens.auth.SignupScreen
import com.william.bizflow.ui.screens.auth.ForgotPasswordScreen
import com.william.bizflow.ui.screens.customer.AddCustomerScreen
import com.william.bizflow.ui.screens.customer.CustomerScreen
import com.william.bizflow.ui.screens.dashboardscreen.DashboardScreen
import com.william.bizflow.ui.screens.home.HomeScreen
import com.william.bizflow.ui.screens.onboardingscreen.OnboardingScreen
import com.william.bizflow.ui.screens.products.ProductListScreen
import com.william.bizflow.ui.screens.products.UpdateProductScreen
import com.william.bizflow.ui.screens.customer.UpdateCustomerScreen
import com.william.bizflow.ui.screens.report.ReportScreen
import com.william.bizflow.ui.screens.sales.AddSaleScreen
import com.william.bizflow.ui.screens.sales.ViewSalesScreen
import com.william.bizflow.ui.screens.splash.SplashScreen
import com.william.bizflow.ui.screens.profile.ProfileScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) { SplashScreen(navController) }

        composable(Routes.ONBOARDING) { OnboardingScreen(navController) }

        composable(Routes.LOGIN) { LoginScreen(navController) }

        composable(Routes.SIGNUP) { SignupScreen(navController) }

        composable(Routes.DASHBOARD) { DashboardScreen(navController) }

        composable(Routes.HOME) { HomeScreen(navController) }

        composable(Routes.ADD_PRODUCT) { AddProductScreen(navController) }

        composable(Routes.VIEW_PRODUCTS) { ProductListScreen(navController) }

        composable(
            Routes.UPDATE_PRODUCT + "/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            UpdateProductScreen(navController, backStackEntry.arguments?.getString("id")!!)
        }

        composable(Routes.CUSTOMER) { CustomerScreen(navController) }

        composable(Routes.ADD_CUSTOMER) { AddCustomerScreen(navController) }

        composable(
            Routes.UPDATE_CUSTOMER + "/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            UpdateCustomerScreen(navController, backStackEntry.arguments?.getString("id")!!)
        }

        composable(Routes.REPORT) { ReportScreen(navController) }

        composable(Routes.ADD_SALE) { AddSaleScreen(navController) }

        composable(Routes.VIEW_SALES) { ViewSalesScreen(navController) }

        composable(Routes.ABOUT) { AboutScreen(navController) }

        composable(Routes.PROFILE) { ProfileScreen(navController) }

        composable(Routes.FORGOT_PASSWORD) { ForgotPasswordScreen(navController) }
    }
}
