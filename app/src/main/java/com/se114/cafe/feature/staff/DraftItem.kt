package com.se114.cafe.feature.staff

import com.se114.cafe.feature.menu.MenuProduct
import java.io.Serializable
import java.math.BigDecimal

internal data class DraftItem(
    val productId: String,
    val variantId: Long,
    val name: String,
    val size: String,
    val price: BigDecimal,
    val quantity: Int
) : Serializable {
    fun resolve(product: MenuProduct?): DraftRow {
        val variant = product?.variants?.find { it.id == variantId }
        val issue = when {
            product == null -> "Món đã ngừng bán"
            !product.available -> "Tạm hết"
            variant == null -> "Kích cỡ đã ngừng bán"
            else -> null
        }
        val line = if (product != null && variant != null) OrderLine(product, variant, quantity) else null
        return DraftRow(this, line, issue)
    }
}

internal data class DraftRow(val item: DraftItem, val line: OrderLine?, val issue: String?) {
    val amount get() = line?.amount ?: item.price * item.quantity.toBigDecimal()
    val priceChanged get() = line != null && item.price.compareTo(line.variant.price) != 0
}
