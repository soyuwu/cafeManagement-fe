package com.se114.cafe.feature.customer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.se114.cafe.feature.menu.MenuSync
import com.se114.cafe.feature.menu.MenuSyncStatus
import com.se114.cafe.feature.menu.dong

@Composable
internal fun CartScreen(
    state: CustomerState,
    sync: MenuSync,
    onQuantityChange: (String, Long, Int) -> Unit,
    onNoteChange: (String) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(contentWindowInsets = WindowInsets(0), topBar = { CustomerTopBar("Giỏ hàng", onBack) },
        bottomBar = {
            Surface(border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)) {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Tạm tính (${state.quantity} món)")
                        Text(dong(state.total), style = MaterialTheme.typography.titleMedium)
                    }
                    Text("Chức năng gửi đơn chưa được kết nối.", style = MaterialTheme.typography.bodyMedium)
                    Button(onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth()) { Text("Đặt món") }
                }
            }
        }) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { MenuSyncStatus(sync) }
            if (state.rows.isEmpty()) item { Text("Giỏ hàng trống") }
            items(state.rows, key = { "${it.item.productId}/${it.item.variantId}" }) { row ->
                val item = row.item
                Surface(shape = MaterialTheme.shapes.large, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)) {
                    Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(item.name, Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
                            Text(dong(row.amount))
                        }
                        Text("Size ${item.size} · ${dong(row.unitPrice)}")
                        row.issue?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                        if (row.priceChanged) Text("Đơn giá đã cập nhật")
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TextButton(onClick = { onQuantityChange(item.productId, item.variantId, 0) }) { Text("Xoá") }
                            Spacer(Modifier.weight(1f))
                            TextButton(onClick = { onQuantityChange(item.productId, item.variantId, item.quantity - 1) }) { Text("−") }
                            Text(item.quantity.toString())
                            TextButton(enabled = row.issue == null && item.quantity < 99,
                                onClick = { onQuantityChange(item.productId, item.variantId, item.quantity + 1) }) { Text("+") }
                        }
                    }
                }
            }
            item {
                OutlinedTextField(state.note, onNoteChange, Modifier.fillMaxWidth(), label = { Text("Ghi chú cho quán") })
            }
        }
    }
}
