package com.se114.cafe.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import androidx.activity.compose.LocalActivity
import com.se114.cafe.feature.admin.AdminScreen
import com.se114.cafe.feature.customer.CartScreen
import com.se114.cafe.feature.customer.CustomerViewModel
import com.se114.cafe.feature.customer.MenuScreen
import com.se114.cafe.feature.customer.OrdersScreen as CustomerOrdersScreen
import com.se114.cafe.feature.menu.rememberMenuSync
import com.se114.cafe.feature.staff.OrderDetailScreen
import com.se114.cafe.feature.staff.CounterOrderScreen
import com.se114.cafe.feature.staff.OrdersScreen
import com.se114.cafe.feature.staff.StaffOrdersViewModel
import com.se114.cafe.ui.theme.CafeRole
import com.se114.cafe.ui.theme.CafeTheme

@Composable
fun AppNavGraph(model: StaffOrdersViewModel = viewModel()) {
    val nav = rememberNavController()
    val state by model.state.collectAsState()
    CafeTheme(CafeRole.Staff) {
        NavHost(nav, startDestination = "orders") {
            composable("orders") {
                OrdersScreen(state, { nav.navigate("orders/$it") }, { nav.navigate("counter") },
                    model::advance, model::toggleAvailability, { nav.navigate("admin") })
            }
            composable("admin") {
                CafeTheme(CafeRole.Admin) { AdminScreen() }
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
}

private const val CustomerMenu = "customer/menu"
private const val CustomerCart = "customer/cart"
private const val CustomerOrders = "customer/orders"

@Composable
fun AppNavGraphCustomer(model: CustomerViewModel = viewModel()) {
    val nav = rememberNavController()
    val state by model.state.collectAsState()
    val activity = LocalActivity.current
    CafeTheme(CafeRole.Customer) {
        NavHost(nav, startDestination = CustomerMenu) {
            composable(CustomerMenu) {
                if (!menuNetworkReady { activity?.finish() }) return@composable
                val sync = rememberMenuSync(model::refreshProducts, model::refreshAvailability)
                MenuScreen(state, sync, model::setQuantity, { nav.navigate(CustomerCart) }, { nav.navigate(CustomerOrders) })
            }
            composable(CustomerCart) {
                if (!menuNetworkReady { nav.popBackStack() }) return@composable
                val sync = rememberMenuSync(model::refreshProducts, model::refreshAvailability)
                CartScreen(state, sync, model::setQuantity, model::setNote) { nav.popBackStack() }
            }
            composable(CustomerOrders) {
                CustomerOrdersScreen(onBackClick = { nav.popBackStack() })
            }
        }
    }
}
