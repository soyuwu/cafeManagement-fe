package com.se114.cafe.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.se114.cafe.feature.auth.LoginScreen
import com.se114.cafe.feature.customer.CartScreen
import com.se114.cafe.feature.customer.MenuScreen
import com.se114.cafe.feature.customer.OrdersScreen

@Composable
fun AppNavGraph(
    startDestination: String = Routes.CUSTOMER_MENU
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.LOGIN) {
            LoginScreen()
        }

        composable(Routes.CUSTOMER_MENU) {
            MenuScreen(
                onViewCart = {
                    navController.navigate(Routes.CUSTOMER_CART)
                },
                onOpenOrders = {
                    navController.navigate(Routes.CUSTOMER_ORDERS)
                }
            )
        }

        composable(Routes.CUSTOMER_CART) {
            CartScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onOrderClick = { _, _ ->
                    navController.navigate(Routes.CUSTOMER_ORDERS)
                }
            )
        }

        composable(Routes.CUSTOMER_ORDERS) {
            OrdersScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onCancelOrder = {
                    // Logic huỷ đơn
                }
            )
        }

        composable(Routes.DASHBOARD) {

        }
    }
}
