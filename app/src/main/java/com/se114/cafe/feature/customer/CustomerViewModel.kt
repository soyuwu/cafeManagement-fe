package com.se114.cafe.feature.customer

import androidx.lifecycle.ViewModel
import com.se114.cafe.feature.menu.MenuData
import com.se114.cafe.feature.menu.MenuFeed
import com.se114.cafe.repository.MenuRepository
import java.math.BigDecimal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal data class CartItem(
    val productId: String,
    val variantId: Long,
    val name: String,
    val size: String,
    val selectedPrice: BigDecimal,
    val quantity: Int
)

internal data class CartRow(val item: CartItem, val unitPrice: BigDecimal, val issue: String?) {
    val amount get() = unitPrice * item.quantity.toBigDecimal()
    val priceChanged get() = unitPrice.compareTo(item.selectedPrice) != 0
}

internal data class CustomerState(
    val menu: MenuData = MenuData(),
    val cart: List<CartItem> = emptyList(),
    val note: String = ""
) {
    val rows: List<CartRow> = cart.map { item ->
        val product = menu.products.find { it.id == item.productId }
        val variant = product?.variants?.find { it.id == item.variantId }
        val issue = when {
            product == null -> "Món đã ngừng bán"
            !product.available -> "Tạm hết"
            variant == null -> "Kích cỡ đã ngừng bán"
            else -> null
        }
        CartRow(item, variant?.price ?: item.selectedPrice, issue)
    }

    val quantity = cart.sumOf { it.quantity }
    val total = rows.sumOf { it.amount }
}

class CustomerViewModel internal constructor(initial: CustomerState, repository: MenuRepository?) : ViewModel() {
    constructor() : this(CustomerState(), MenuRepository())

    private val feed = repository?.let(::MenuFeed)
    private val mutableState = MutableStateFlow(initial)
    internal val state = mutableState.asStateFlow()

    internal suspend fun refreshProducts() {
        val menu = feed?.snapshot() ?: return
        mutableState.update { it.copy(menu = menu) }
    }

    internal suspend fun refreshAvailability() {
        val menu = feed?.availability(state.value.menu) ?: return
        mutableState.update { it.copy(menu = menu) }
    }

    internal fun setNote(note: String) = mutableState.update { it.copy(note = note) }

    internal fun setQuantity(productId: String, variantId: Long, quantity: Int) {
        if (quantity !in 0..99) return
        mutableState.update { current ->
            val existing = current.cart.find { it.productId == productId && it.variantId == variantId }
            if (quantity == 0) return@update current.copy(cart = current.cart.filterNot { it == existing })
            val product = current.menu.products.find { it.id == productId && it.available }
            val variant = product?.variants?.find { it.id == variantId }
            val updated = when {
                existing != null && quantity < existing.quantity -> existing.copy(quantity = quantity)
                product == null || variant == null -> return@update current
                existing != null -> existing.copy(quantity = quantity)
                else -> CartItem(productId, variantId, product.name, variant.size, variant.price, quantity)
            }
            current.copy(cart = if (existing == null) current.cart + updated else current.cart.map {
                if (it == existing) updated else it
            })
        }
    }
}
