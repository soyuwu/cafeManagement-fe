package com.se114.cafe.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.se114.cafe.feature.staff.*

@Composable
fun AppNavGraph(model: StaffOrdersViewModel = viewModel()) {
    val nav = rememberNavController()
    val state by model.state.collectAsState()
    NavHost(nav, startDestination = "orders") {
        composable("orders") {
            OrdersScreen(state, { nav.navigate("orders/$it") }, { nav.navigate("counter") },
                model::advance, model::toggleAvailability)
        }
        composable("counter") {
            if (!menuNetworkReady { nav.popBackStack() }) return@composable
            CounterOrderScreen(state.products, state.categories, model::refreshProducts, model::refreshAvailability, { nav.popBackStack() }) { lines, customer, table, note, discount ->
                val number = model.create(lines, customer, table, note, discount)
                if (number != null) nav.navigate("orders/$number") { popUpTo("counter") { inclusive = true } }
            }
        }
        composable("orders/{number}", arguments = listOf(navArgument("number") { type = NavType.IntType })) { entry ->
            val number = entry.arguments?.getInt("number")
            val order = state.orders.find { it.number == number }
            if (order == null) {
                LaunchedEffect(number) { nav.popBackStack() }
            } else {
                OrderDetailScreen(order, { nav.popBackStack() }, { model.advance(order.number, order.stage) },
                    { model.requestCancellation(order.number) }, { qr, ref -> model.collect(order.number, qr, ref) })
            }
        }
    }
}
