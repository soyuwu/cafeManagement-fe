package com.se114.cafe.feature.menu

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.Locale

data class MenuProduct(
    val id: String, val name: String, val category: String,
    val prices: Map<String, Int>, val available: Boolean = true
)

fun dong(value: Int): String = NumberFormat.getIntegerInstance(Locale.forLanguageTag("vi-VN")).format(value) + "đ"

@Composable
fun MenuCatalog(
    products: List<MenuProduct>,
    category: String,
    quantities: Map<String, Int>,
    onCategoryChange: (String) -> Unit,
    onProductClick: (MenuProduct) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    Column(modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            products.map { it.category }.distinct().forEach { label ->
                val selected = label == category
                Surface(shape = CircleShape, color = if (selected) colors.primary else colors.surface,
                    contentColor = if (selected) colors.onPrimary else colors.onSurface,
                    border = BorderStroke(1.dp, if (selected) colors.primary else colors.outline)) {
                    Box(Modifier.selectable(selected, role = Role.Tab) { onCategoryChange(label) }
                        .heightIn(min = 44.dp).padding(horizontal = 16.dp), contentAlignment = Alignment.Center) {
                        Text(label, style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
        LazyVerticalGrid(GridCells.Fixed(2), contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(products.filter { it.category == category }, key = { it.id }) { product ->
                MenuProductCard(product, quantities[product.id] ?: 0, { onProductClick(product) })
            }
        }
    }
}

@Composable
fun MenuProductCard(product: MenuProduct, quantity: Int, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    Surface(modifier = modifier, shape = MaterialTheme.shapes.large,
        color = if (product.available) colors.surface else colors.surfaceVariant,
        border = if (quantity > 0) BorderStroke(2.dp, colors.secondary) else BorderStroke(1.dp, colors.outline)) {
        Column(Modifier.clickable(enabled = product.available, role = Role.Button, onClick = onClick)
            .heightIn(min = 76.dp).padding(12.dp), verticalArrangement = Arrangement.Center) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(product.name, Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
                if (quantity > 0) Surface(shape = CircleShape, color = colors.secondary) {
                    Text(quantity.toString(), Modifier.padding(horizontal = 7.dp, vertical = 2.dp), style = MaterialTheme.typography.labelMedium)
                }
            }
            Text(if (product.available) "từ " + dong(product.prices.values.min()) else "Tạm hết",
                color = colors.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
