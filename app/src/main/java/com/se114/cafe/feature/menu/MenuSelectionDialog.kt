package com.se114.cafe.feature.menu

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MenuSelectionDialog(
    product: MenuProduct,
    quantitiesByVariant: Map<Long, Int>,
    onQuantityChange: (variantId: Long, quantity: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val initial = product.variants.firstOrNull { it.id in quantitiesByVariant }
        ?: product.variants.firstOrNull() ?: return
    var selectedId by rememberSaveable(product.id) { mutableLongStateOf(initial.id) }
    val selected = product.variants.firstOrNull { it.id == selectedId } ?: initial
    var quantity by rememberSaveable(product.id, selected.id) { mutableIntStateOf(quantitiesByVariant[selected.id] ?: 1) }
    AlertDialog(onDismissRequest = onDismiss, containerColor = MaterialTheme.colorScheme.surface,
        title = { Text(product.name) }, text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    product.variants.forEach { variant ->
                        FilterChip(selected = selected.id == variant.id, onClick = { selectedId = variant.id }, label = { Text(variant.size + " · " + dong(variant.price)) }, modifier = Modifier.heightIn(min = 48.dp))
                    }
                }
                Text(dong(selected.price))
                if (!product.available) Text("Tạm hết")
                if (product.available) Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Số lượng", Modifier.weight(1f))
                    TextButton(onClick = { quantity-- }, enabled = quantity > 0) { Text("−") }
                    Text(quantity.toString())
                    TextButton(onClick = { quantity++ }, enabled = quantity < 99) { Text("+") }
                }
            }
        }, confirmButton = { if (product.available) TextButton(onClick = { onQuantityChange(selected.id, quantity) }) {
            Text(if (quantity == 0) "Xoá khỏi đơn" else "Cập nhật · " + dong(selected.price * quantity.toBigDecimal()))
        } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Đóng") } })
}
