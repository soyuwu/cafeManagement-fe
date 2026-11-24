package com.se114.cafe.feature.customer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.se114.cafe.feature.menu.*

@Composable
internal fun MenuScreen(
    state: CustomerState,
    sync: MenuSync,
    onQuantityChange: (String, Long, Int) -> Unit,
    onViewCart: () -> Unit,
    onOpenOrders: () -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf<String?>(null) }
    var editing by rememberSaveable { mutableStateOf<String?>(null) }
    val (products, categories) = state.menu
    val category = selectedCategory?.takeIf { id -> categories.any { it.id == id } } ?: categories.firstOrNull()?.id
    Scaffold(contentWindowInsets = WindowInsets(0), bottomBar = {
        if (state.quantity > 0) {
            Surface(onClick = onViewCart, color = MaterialTheme.colorScheme.primary, shape = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                Row(Modifier.heightIn(min = 52.dp).padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Xem giỏ hàng · ${state.quantity} món")
                    Text(dong(state.total))
                }
            }
        }
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 12.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Text("Hôm nay uống gì?", Modifier.weight(1f), style = MaterialTheme.typography.headlineSmall)
                Surface(onClick = onOpenOrders, shape = MaterialTheme.shapes.medium,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)) {
                    Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                        Icon(Icons.AutoMirrored.Outlined.ReceiptLong, contentDescription = "Đơn của tôi")
                    }
                }
            }
            OutlinedTextField(query, { query = it }, Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                singleLine = true, label = { Text("Tìm món") }, leadingIcon = { Icon(Icons.Outlined.Search, null) })
            MenuSyncStatus(sync)
            if (categories.isNotEmpty() || (!sync.loading && sync.error == null)) {
                MenuCatalog(products.filter { it.name.contains(query.trim(), ignoreCase = true) }, categories, category,
                    state.cart.groupBy { it.productId }.mapValues { (_, lines) -> lines.sumOf { it.quantity } },
                    { selectedCategory = it }, { editing = it.id }, Modifier.weight(1f))
            }
        }
    }
    products.find { it.id == editing }?.let { product ->
        MenuSelectionDialog(product, state.cart.filter { it.productId == product.id }.associate { it.variantId to it.quantity },
            { variant, quantity ->
                onQuantityChange(product.id, variant, quantity)
                editing = null
            }, { editing = null })
    }
}
