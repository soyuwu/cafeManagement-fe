package com.se114.cafe.feature.menu

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MenuSelectionDialog(
    product: MenuProduct,
    quantitiesBySize: Map<String, Int>,
    onQuantityChange: (size: String, quantity: Int) -> Unit,
    onDismiss: () -> Unit
) {
    var size by rememberSaveable(product.id) { mutableStateOf(quantitiesBySize.keys.firstOrNull() ?: product.prices.keys.first()) }
    var quantity by rememberSaveable(product.id, size) { mutableIntStateOf(quantitiesBySize[size] ?: 1) }
    AlertDialog(onDismissRequest = onDismiss, containerColor = MaterialTheme.colorScheme.surface,
        title = { Text(product.name) }, text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                if (size.isNotEmpty()) Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    product.prices.keys.forEach {
                        FilterChip(selected = size == it, onClick = { size = it }, label = { Text(it) }, modifier = Modifier.heightIn(min = 48.dp))
                    }
                }
                Text(dong(product.prices.getValue(size)))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Số lượng", Modifier.weight(1f))
                    TextButton(onClick = { quantity-- }, enabled = quantity > 0) { Text("−") }
                    Text(quantity.toString())
                    TextButton(onClick = { quantity++ }, enabled = quantity < 99) { Text("+") }
                }
            }
        }, confirmButton = { TextButton(onClick = { onQuantityChange(size, quantity) }) {
            Text(if (quantity == 0) "Xoá khỏi đơn" else "Cập nhật · " + dong(product.prices.getValue(size) * quantity))
        } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Đóng") } })
}
