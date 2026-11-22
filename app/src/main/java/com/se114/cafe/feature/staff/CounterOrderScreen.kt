package com.se114.cafe.feature.staff

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.se114.cafe.feature.menu.*

@Composable
internal fun CounterOrderScreen(products: List<MenuProduct>, back: () -> Unit,
    create: (List<OrderLine>, String, String, String, Int) -> Unit) {
    var category by rememberSaveable { mutableStateOf(products.first().category) }
    var editing by rememberSaveable { mutableStateOf<String?>(null) }
    var amounts by rememberSaveable { mutableStateOf(hashMapOf<String, Int>()) }
    var table by rememberSaveable { mutableStateOf("") }
    var customer by rememberSaveable { mutableStateOf("") }
    var note by rememberSaveable { mutableStateOf("") }
    var discount by rememberSaveable { mutableStateOf("") }
    var tableMenu by remember { mutableStateOf(false) }
    val lines = products.flatMap { product ->
        product.prices.keys.mapNotNull { size ->
            amounts["${product.id}/$size"]?.let { OrderLine(product, size, it) }
        }
    }
    val subtotal = lines.sumOf { it.amount }
    val reduction = discount.toIntOrNull() ?: 0
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        StaffHeader("Tạo đơn tại quầy", back)
        // On compact windows the entire form can scroll while the grid has its own viewport.
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val compact = maxHeight < 520.dp
            val form = if (compact) Modifier.verticalScroll(rememberScrollState()) else Modifier.fillMaxHeight()
            Column(form, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface {
                    Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Column(Modifier.weight(1f)) {
                            Text("Bàn (tuỳ chọn)", style = MaterialTheme.typography.labelMedium)
                            Box {
                                OutlinedButton(onClick = { tableMenu = true }, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                                    shape = MaterialTheme.shapes.medium) { Text(table.ifBlank { "Mang đi" } + " ▾") }
                                DropdownMenu(expanded = tableMenu, onDismissRequest = { tableMenu = false }) {
                                    (listOf("") + (1..10).map { "Bàn T" + it.toString().padStart(2, '0') }).forEach {
                                        DropdownMenuItem(text = { Text(it.ifBlank { "Mang đi" }) }, onClick = { table = it; tableMenu = false })
                                    }
                                }
                            }
                        }
                        Column(Modifier.weight(1f)) {
                            Text("Khách hàng", style = MaterialTheme.typography.labelMedium)
                            Entry(customer, { customer = it }, "Vãng lai")
                        }
                    }
                }
                MenuCatalog(products, category, lines.groupBy { it.product.id }.mapValues { (_, rows) -> rows.sumOf { it.quantity } },
                    { category = it }, { editing = it.id }, if (compact) Modifier.height(244.dp) else Modifier.weight(1f))
                Surface(shape = androidx.compose.foundation.shape.RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)) {
                    Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Đơn hiện tại · ${lines.sumOf { it.quantity }} món", style = MaterialTheme.typography.titleMedium)
                        Column(Modifier.heightIn(max = 96.dp).verticalScroll(rememberScrollState())) {
                            lines.forEach { PriceRow(it.label, dong(it.amount)) }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Entry(note, { note = it }, "Ghi chú", Modifier.weight(1.5f))
                            Entry(discount, { if (it.length <= 9 && it.all { ch -> ch in '0'..'9' }) discount = it },
                                "Giảm giá (đ)", Modifier.weight(1f), numeric = true)
                        }
                        HorizontalDivider()
                        PriceRow("Tổng", dong((subtotal - reduction).coerceAtLeast(0)))
                        if (reduction > subtotal) Text("Giảm giá vượt tổng đơn", color = MaterialTheme.colorScheme.error)
                        OrderAction("Tạo đơn & thu tiền", { create(lines, customer, table, note, reduction) },
                            enabled = lines.isNotEmpty() && reduction <= subtotal)
                    }
                }
            }
        }
    }
    products.find { it.id == editing }?.let { product ->
        MenuSelectionDialog(product, lines.filter { it.product.id == product.id }.associate { it.size to it.quantity },
            { size, quantity ->
                val key = "${product.id}/$size"
                amounts = HashMap(amounts).apply { if (quantity == 0) remove(key) else put(key, quantity) }
                editing = null
            }, { editing = null })
    }
}

@Composable
internal fun Entry(value: String, change: (String) -> Unit, hint: String, modifier: Modifier = Modifier, numeric: Boolean = false) {
    OutlinedTextField(value, change, modifier, singleLine = true, shape = MaterialTheme.shapes.medium,
        placeholder = { Text(hint, style = MaterialTheme.typography.bodyMedium) },
        keyboardOptions = KeyboardOptions(keyboardType = if (numeric) KeyboardType.Number else KeyboardType.Text))
}
