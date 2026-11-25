package com.se114.cafe.feature.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
internal fun ProductsScreen(products: List<Product>, list: LazyListState, save: (Product) -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    var filter by rememberSaveable { mutableStateOf<Availability?>(null) }
    var editor by rememberSaveable { mutableStateOf<Int?>(null) }
    val visible = products.filter {
        it.name.contains(query.trim(), ignoreCase = true) && (filter == null || it.status == filter)
    }
    ManagementList(
        "sản phẩm", products.size, visible, list, query, { query = it },
        Product::id, Product::name, { editor = 0 }, { editor = it.id },
        filters = { reset ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                (listOf(null) + Availability.entries).forEach { status ->
                    Filter(status?.label ?: "Tất cả", filter == status, Modifier.weight(1f)) {
                        filter = status
                        reset()
                    }
                }
            }
        }
    ) { product ->
        Muted(product.category)
        Text(product.name, style = MaterialTheme.typography.titleMedium)
        Text("Từ ${money(product.price)}", color = MaterialTheme.colorScheme.secondary, style = MaterialTheme.typography.titleMedium)
        Muted("Kích cỡ: ${product.sizes}")
        val available = product.status == Availability.Available
        Surface(
            shape = MaterialTheme.shapes.small,
            color = if (available) Color(0xFFDDEBDD) else MaterialTheme.colorScheme.secondaryContainer,
            contentColor = if (available) Color(0xFF2F5D3A) else MaterialTheme.colorScheme.onSurfaceVariant
        ) {
            Text(product.status.label, Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelMedium)
        }
    }
    editor?.let { id ->
        val product = products.find { it.id == id }
        var name by rememberSaveable { mutableStateOf(product?.name.orEmpty()) }
        var category by rememberSaveable { mutableStateOf(product?.category.orEmpty()) }
        var price by rememberSaveable { mutableStateOf(product?.price?.toString().orEmpty()) }
        var sizes by rememberSaveable { mutableStateOf(product?.sizes.orEmpty()) }
        var status by rememberSaveable { mutableStateOf(product?.status ?: Availability.Available) }
        val value = price.toIntOrNull()
        Editor(
            if (id == 0) "Thêm sản phẩm" else "Chỉnh sửa sản phẩm",
            name.isNotBlank() && category.isNotBlank() && sizes.isNotBlank() && value != null && value > 0,
            { editor = null },
            {
                if (value != null) {
                    save(Product(id, name.trim(), category.trim(), value, sizes.trim(), status))
                    editor = null
                }
            }
        ) {
            EditField("Tên sản phẩm", name) { name = it }
            EditField("Danh mục", category) { category = it }
            EditField("Giá (đ)", price, KeyboardType.Number) { price = it }
            EditField("Kích cỡ", sizes) { sizes = it }
            Text("Trạng thái", style = MaterialTheme.typography.labelLarge)
            Availability.entries.forEach { option ->
                Filter(option.label, status == option, Modifier.fillMaxWidth()) { status = option }
            }
        }
    }
}

@Composable
internal fun EmployeesScreen(employees: List<Employee>, list: LazyListState, save: (Employee) -> Unit, delete: (Int) -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    var editor by rememberSaveable { mutableStateOf<Int?>(null) }
    var pendingDelete by rememberSaveable { mutableStateOf<Int?>(null) }
    ManagementList(
        "nhân viên", employees.size, employees.filter { it.name.contains(query.trim(), ignoreCase = true) },
        list, query, { query = it }, Employee::id, Employee::name, { editor = 0 }, { editor = it.id },
        remove = { pendingDelete = it.id }, searchIcon = true
    ) { employee ->
        Text(employee.name, style = MaterialTheme.typography.titleMedium)
        Text(employee.role, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
    }
    editor?.let { id ->
        val employee = employees.find { it.id == id }
        var name by rememberSaveable { mutableStateOf(employee?.name.orEmpty()) }
        var role by rememberSaveable { mutableStateOf(employee?.role.orEmpty()) }
        Editor(
            if (id == 0) "Thêm nhân viên" else "Chỉnh sửa nhân viên",
            name.isNotBlank() && role.isNotBlank(), { editor = null },
            {
                save(Employee(id, name.trim(), role.trim()))
                editor = null
            }
        ) {
            EditField("Họ và tên", name) { name = it }
            EditField("Vai trò", role) { role = it }
        }
    }
    employees.find { it.id == pendingDelete }?.let { employee ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text("Xóa nhân viên") },
            text = { Text("Xóa ${employee.name} khỏi danh sách mẫu?") },
            confirmButton = {
                TextButton(onClick = {
                    delete(employee.id)
                    pendingDelete = null
                }) { Text("Xóa", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { pendingDelete = null }) { Text("Hủy") } }
        )
    }
}

@Composable
private fun <T> ManagementList(
    kind: String, total: Int, entries: List<T>, list: LazyListState, query: String,
    changeQuery: (String) -> Unit, id: (T) -> Int, name: (T) -> String, add: () -> Unit, edit: (T) -> Unit,
    remove: ((T) -> Unit)? = null, searchIcon: Boolean = false,
    filters: @Composable (() -> Unit) -> Unit = {},
    details: @Composable ColumnScope.(T) -> Unit
) {
    var selection by rememberSaveable { mutableStateOf<Int?>(null) }
    val selected = entries.firstOrNull { id(it) == selection }
    val scope = rememberCoroutineScope()
    val colors = MaterialTheme.colorScheme
    val reset: () -> Unit = {
        selection = null
        scope.launch { list.scrollToItem(0) }
    }
    Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Banner("Danh sách minh họa, chưa kết nối API.")
        Spacer(Modifier.height(4.dp))
        OutlinedTextField(
            query, {
                changeQuery(it)
                reset()
            }, Modifier.fillMaxWidth(),
            label = { Text("Tìm $kind") },
            leadingIcon = if (searchIcon) { { Icon(Icons.Outlined.Search, null) } } else null,
            singleLine = true, shape = MaterialTheme.shapes.medium
        )
        filters(reset)
        HorizontalDivider()
        if (selected != null) {
            Text("Đã chọn: ${name(selected)}", style = MaterialTheme.typography.bodyMedium)
            Row {
                TextButton(onClick = { edit(selected) }) { Text("Chỉnh sửa", color = colors.secondary) }
                if (remove != null) {
                    TextButton(onClick = {
                        remove(selected)
                        selection = null
                    }) { Text("Xóa", color = colors.error) }
                }
                TextButton(onClick = { selection = null }) { Text("Bỏ chọn", color = colors.onSurfaceVariant) }
            }
        }
    }
    LazyColumn(state = list, contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Danh sách $kind", Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
                    Button(add, Modifier.size(48.dp), shape = MaterialTheme.shapes.medium, contentPadding = PaddingValues(0.dp)) {
                        Icon(Icons.Outlined.Add, "Thêm $kind")
                    }
                }
                Muted("Hiển thị ${entries.size} / $total $kind")
            }
        }
        if (entries.isEmpty()) item { Muted("Không tìm thấy $kind.") }
        items(entries, key = { id(it) }) { entry ->
            val active = id(entry) == selection
            Panel(active = active, click = { selection = id(entry) }) {
                details(entry)
                if (active) Text("Đã chọn", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
private fun Editor(title: String, valid: Boolean, dismiss: () -> Unit, save: () -> Unit, fields: @Composable ColumnScope.() -> Unit) {
    AlertDialog(
        onDismissRequest = dismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text(title, style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp), content = fields)
        },
        confirmButton = { Button(save, enabled = valid) { Text("Lưu") } },
        dismissButton = { TextButton(dismiss) { Text("Hủy") } }
    )
}

@Composable
private fun EditField(label: String, value: String, type: KeyboardType = KeyboardType.Text, change: (String) -> Unit) {
    OutlinedTextField(value, change, Modifier.fillMaxWidth(), label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = type), singleLine = true)
}
