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
import com.se114.cafe.network.dto.ProductVariantResponse
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

data class MenuCategory(val id: String, val name: String)

data class MenuProduct(
    val id: String, val name: String, val categoryId: String,
    val variants: List<ProductVariantResponse>, val available: Boolean = true,
    val minPrice: BigDecimal? = null
)

fun dong(value: BigDecimal): String = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN")).format(value) + "đ"

@Composable
fun MenuCatalog(
    products: List<MenuProduct>,
    categories: List<MenuCategory>,
    category: String?,
    quantities: Map<String, Int>,
    onCategoryChange: (String) -> Unit,
    onProductClick: (MenuProduct) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    Column(modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        val visible = products.filter { it.categoryId == category }
        if (categories.isEmpty()) {
            Text("Chưa có sản phẩm", Modifier.padding(horizontal = 16.dp))
        }
        Row(Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            categories.forEach { item ->
                val selected = item.id == category
                Surface(shape = CircleShape, color = if (selected) colors.primary else colors.surface,
                    contentColor = if (selected) colors.onPrimary else colors.onSurface,
                    border = BorderStroke(1.dp, if (selected) colors.primary else colors.outline)) {
                    Box(Modifier.selectable(selected, role = Role.Tab) { onCategoryChange(item.id) }
                        .heightIn(min = 44.dp).padding(horizontal = 16.dp), contentAlignment = Alignment.Center) {
                        Text(item.name, style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
        if (categories.isNotEmpty() && visible.isEmpty()) {
            Text("Chưa có món trong danh mục này", Modifier.padding(horizontal = 16.dp))
        }
        LazyVerticalGrid(GridCells.Fixed(2), contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(visible, key = { it.id }) { product ->
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
        Column(Modifier.clickable(enabled = product.variants.isNotEmpty(), role = Role.Button, onClick = onClick)
            .heightIn(min = 76.dp).padding(12.dp), verticalArrangement = Arrangement.Center) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(product.name, Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
                if (quantity > 0) Surface(shape = CircleShape, color = colors.secondary) {
                    Text(quantity.toString(), Modifier.padding(horizontal = 7.dp, vertical = 2.dp), style = MaterialTheme.typography.labelMedium)
                }
            }
            product.minPrice?.let { Text("Từ " + dong(it), style = MaterialTheme.typography.bodyMedium) }
            if (!product.available) Text("Tạm hết", color = colors.onSurfaceVariant)
            if (product.variants.isEmpty()) Text("Chưa có kích cỡ", color = colors.onSurfaceVariant)
        }
    }
}
