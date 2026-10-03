package com.se114.cafe.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*

@Composable
fun AppNavGraph() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {

        composable(Routes.LOGIN) {

        }

        composable(Routes.DASHBOARD) {

        }

    }
}