package com.se114.cafe.feature.staff

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.se114.cafe.feature.menu.dong
import java.time.Duration
import java.time.LocalDateTime

@Composable
internal fun OrdersScreen(state: StaffState, open: (Int) -> Unit, create: () -> Unit,
    advance: (Int, Stage) -> Unit, toggleAvailability: (String) -> Unit) {
    var stage by rememberSaveable { mutableStateOf(Stage.Pending) }
    var inventory by rememberSaveable { mutableStateOf(false) }
    val colors = MaterialTheme.colorScheme
    val orders = state.orders.filter { it.stage == stage }
    Column(Modifier.fillMaxSize().background(colors.background)) {
        Column(Modifier.fillMaxWidth().background(colors.primary).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Ca sáng · Nhân viên Giang", color = Color.White, style = MaterialTheme.typography.bodySmall)
                    Text("Đơn hàng", color = Color.White, style = MaterialTheme.typography.headlineSmall)
                }
                OutlinedButton(onClick = { inventory = true }, shape = MaterialTheme.shapes.medium) { Text("Hết món", color = Color.White) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Stage.entries.filter { it != Stage.Served }.forEach { tab ->
                    val active = stage == tab
                    Surface(onClick = { stage = tab }, Modifier.weight(1f), shape = MaterialTheme.shapes.medium,
                        color = if (active) colors.surface else colors.primary,
                        contentColor = if (active) colors.primary else Color.White, border = BorderStroke(1.dp, colors.outline)) {
                        Box(Modifier.heightIn(min = 44.dp), contentAlignment = Alignment.Center) {
                            Text("${tab.tab} · ${state.orders.count { it.stage == tab }}", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }
        LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (orders.isEmpty()) item { Text("Chưa có đơn trong mục này") }
            itemsIndexed(orders, key = { _, order -> order.number }) { index, order ->
                OrderPanel(Modifier.clickable { open(order.number) }, emphasized = index == 0) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(order.code, style = MaterialTheme.typography.titleLarge)
                        Tag(if (order.fromApp) "APP KHÁCH" else "QUẦY", colors.onSurface, colors.surfaceVariant)
                        Spacer(Modifier.weight(1f))
                        val minutes = Duration.between(order.placed, LocalDateTime.now()).toMinutes().coerceAtLeast(0)
                        Text(if (minutes == 0L) "Vừa xong" else "$minutes phút trước", style = MaterialTheme.typography.bodySmall)
                    }
                    Text(order.location, color = colors.onSurfaceVariant)
                    order.lines.forEach { Text(it.label) }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PaymentTag(order.paid)
                        Spacer(Modifier.weight(1f))
                        Text(dong(order.total), style = MaterialTheme.typography.titleMedium)
                    }
                    OrderAction(stage.action, { advance(order.number, stage) })
                }
            }
        }
        Surface { Box(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) { OrderAction("＋  Tạo đơn tại quầy", create) } }
    }
    if (inventory) AlertDialog(onDismissRequest = { inventory = false }, containerColor = colors.surface,
        title = { Text("Tình trạng món") }, text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                state.products.forEach { product ->
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(product.name)
                            Text(if (product.available) "Đang bán" else "Tạm hết", style = MaterialTheme.typography.bodySmall)
                        }
                        Switch(checked = product.available, onCheckedChange = { toggleAvailability(product.id) })
                    }
                }
            }
        }, confirmButton = { TextButton(onClick = { inventory = false }) { Text("Xong") } })
}
