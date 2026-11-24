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
import java.math.BigDecimal

@Composable
internal fun CounterOrderScreen(products: List<MenuProduct>, categories: List<MenuCategory>,
    refreshProducts: suspend () -> Unit, refreshAvailability: suspend () -> Unit, back: () -> Unit,
    create: (List<OrderLine>, String, String, String, BigDecimal) -> Unit) {
    val sync = rememberMenuSync(refreshProducts, refreshAvailability)
    var selectedCategory by rememberSaveable { mutableStateOf<String?>(null) }
    val category = selectedCategory?.takeIf { id -> categories.any { it.id == id } } ?: categories.firstOrNull()?.id
    var editing by rememberSaveable { mutableStateOf<String?>(null) }
    var draft by rememberSaveable { mutableStateOf(arrayListOf<DraftItem>()) }
    var replacing by rememberSaveable { mutableStateOf<Long?>(null) }
    var table by rememberSaveable { mutableStateOf("") }
    var customer by rememberSaveable { mutableStateOf("") }
    var note by rememberSaveable { mutableStateOf("") }
    var discount by rememberSaveable { mutableStateOf("") }
    var tableMenu by remember { mutableStateOf(false) }
    val byId = products.associateBy { it.id }
    val rows = draft.map { it.resolve(byId[it.productId]) }
    val lines = rows.mapNotNull { it.line }
    val subtotal = rows.sumOf { it.amount }
    val reduction = discount.toBigDecimalOrNull() ?: BigDecimal.ZERO
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
                MenuSyncStatus(sync)
                if (categories.isNotEmpty() || products.isNotEmpty() || (!sync.loading && sync.error == null)) MenuCatalog(products, categories, category, draft.groupBy { it.productId }.mapValues { (_, rows) -> rows.sumOf { it.quantity } },
                    { selectedCategory = it }, { replacing = null; editing = it.id }, if (compact) Modifier.height(244.dp) else Modifier.weight(1f))
                Surface(shape = androidx.compose.foundation.shape.RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)) {
                    Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Đơn hiện tại · ${draft.sumOf { it.quantity }} món", style = MaterialTheme.typography.titleMedium)
                        Column(Modifier.heightIn(max = 96.dp).verticalScroll(rememberScrollState())) {
                            rows.forEach { row ->
                                val item = row.item
                                PriceRow(row.line?.label ?: "${item.quantity} × ${item.name} (${item.size})", dong(row.amount))
                                if (row.priceChanged) Text("Đơn giá đã cập nhật")
                                if (row.issue != null) {
                                    Text(row.issue, color = MaterialTheme.colorScheme.error)
                                    Row {
                                        TextButton(onClick = { draft = ArrayList(draft - item) }) { Text("Xóa") }
                                        val product = byId[item.productId]
                                        if (product?.available == true && product.variants.isNotEmpty()) {
                                            TextButton(onClick = { replacing = item.variantId; editing = product.id }) { Text("Đổi kích cỡ") }
                                        }
                                    }
                                }
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Entry(note, { note = it }, "Ghi chú", Modifier.weight(1.5f))
                            Entry(discount, { if (it.length <= 9 && it.all { ch -> ch in '0'..'9' }) discount = it },
                                "Giảm giá (đ)", Modifier.weight(1f), numeric = true)
                        }
                        HorizontalDivider()
                        PriceRow("Tổng", dong((subtotal - reduction).coerceAtLeast(BigDecimal.ZERO)))
                        if (reduction > subtotal) Text("Giảm giá vượt tổng đơn", color = MaterialTheme.colorScheme.error)
                        OrderAction("Tạo đơn & thu tiền", { create(lines, customer, table, note, reduction) },
                            enabled = !sync.loading && lines.isNotEmpty() && rows.all { it.issue == null } && reduction <= subtotal)
                    }
                }
            }
        }
    }
    products.find { it.id == editing }?.let { product ->
        val quantities = draft.filter { it.productId == product.id }.associate { it.variantId to it.quantity }
        val previous = replacing?.let { quantities[it] } ?: 0
        val targetQuantities = if (previous == 0) quantities else product.variants.associate {
            it.id to ((quantities[it.id] ?: 0) + previous).coerceAtMost(99)
        }
        MenuSelectionDialog(product, targetQuantities,
            { variantId, quantity ->
                val variant = product.variants.first { it.id == variantId }
                draft = ArrayList(draft.filterNot { it.productId == product.id && (it.variantId == variantId || it.variantId == replacing) })
                    .apply { if (quantity > 0) add(DraftItem(product.id, variantId, product.name, variant.size, variant.price, quantity)) }
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
