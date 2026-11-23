package com.se114.cafe.feature.staff

import androidx.lifecycle.ViewModel
import java.math.BigDecimal
import com.se114.cafe.network.dto.ProductVariantResponse
import com.se114.cafe.feature.menu.MenuProduct
import com.se114.cafe.feature.menu.MenuCategory
import com.se114.cafe.repository.MenuRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

internal enum class Stage(val tab: String, val label: String, val action: String) {
    Pending("Mới", "Mới", "Nhận đơn · Bắt đầu pha"),
    Preparing("Pha chế", "Đang pha", "Báo sẵn sàng"),
    Ready("Sẵn sàng", "Sẵn sàng", "Đánh dấu đã phục vụ"),
    Served("", "Đã phục vụ", "")
}

internal data class OrderLine(val product: MenuProduct, val variant: ProductVariantResponse, val quantity: Int, val options: String = "") {
    val amount get() = variant.price * quantity.toBigDecimal()
    val label get() = "$quantity × ${product.name}" + if (variant.size.isEmpty()) "" else " (${variant.size})"
}
internal data class Order(
    val number: Int, val lines: List<OrderLine>, val customer: String, val table: String,
    val placed: LocalDateTime, val fromApp: Boolean = false, val stage: Stage = Stage.Pending,
    val paid: Boolean = false, val note: String = "", val discount: BigDecimal = BigDecimal.ZERO,
    val history: List<String> = emptyList(), val cancellationRequested: Boolean = false
) {
    val code get() = "#A-" + number.toString().padStart(4, '0')
    val total get() = (lines.sumOf { it.amount } - discount).coerceAtLeast(BigDecimal.ZERO)
    val location get() = listOf(table.ifBlank { "Mang đi" }, customer.ifBlank { "Khách vãng lai" }).joinToString(" · ")
}
internal fun LocalDateTime.clock(): String = format(DateTimeFormatter.ofPattern("HH:mm"))

internal data class StaffState(
    val products: List<MenuProduct>,
    val orders: List<Order>,
    val categories: List<MenuCategory> = emptyList()
)

class StaffOrdersViewModel internal constructor(
    products: List<MenuProduct>,
    private val repository: MenuRepository? = null
) : ViewModel() {
    constructor() : this(emptyList(), MenuRepository())

    private val mutableState = MutableStateFlow(StaffState(products, emptyList(),
        products.map { MenuCategory(it.categoryId, it.categoryId) }.distinct()))
    internal val state = mutableState.asStateFlow()

    private var snapshotLoaded = false

    internal suspend fun refreshProducts() {
        val result = repository?.snapshot() ?: return
        if (result.notModified && snapshotLoaded) return
        val snapshot = result.body
        val categories = snapshot.categories.mapNotNull { category ->
            category.id?.let { MenuCategory(it.toString(), category.name) }
        }
        val categoryIds = categories.map { it.id }.toSet()
        val products = snapshot.products.mapNotNull { product ->
            val id = product.id ?: return@mapNotNull null
            val category = product.categoryId?.toString()?.takeIf { it in categoryIds } ?: "other"
            MenuProduct(id.toString(), product.name, category, product.variants.orEmpty(),
                product.isAvailable, product.minPrice)
        }
        val tabs = if (products.any { it.categoryId == "other" }) categories + MenuCategory("other", "Khác") else categories
        mutableState.update { it.copy(products = products, categories = tabs) }
        snapshotLoaded = true
    }

    internal suspend fun refreshAvailability() {
        val availability = repository?.availability()?.associate { it.productId.toString() to it.isAvailable } ?: return
        if (availability.keys != state.value.products.map { it.id }.toSet()) {
            refreshProducts()
        } else {
            mutableState.update { current ->
                current.copy(products = current.products.map { it.copy(available = availability.getValue(it.id)) })
            }
        }
    }

    internal fun toggleAvailability(id: String) = mutableState.update { state ->
        state.copy(products = state.products.map { if (it.id == id) it.copy(available = !it.available) else it })
    }

    private fun change(number: Int, transform: (Order) -> Order) = mutableState.update { state ->
        state.copy(orders = state.orders.map { if (it.number == number) transform(it) else it })
    }

    internal fun advance(number: Int, expected: Stage) = change(number) { order ->
        if (order.stage != expected || expected == Stage.Served) order else {
            val next = Stage.entries[expected.ordinal + 1]
            order.copy(stage = next, history = order.history + "${LocalDateTime.now().clock()}  ${next.label}")
        }
    }

    internal fun requestCancellation(number: Int) = change(number) { order ->
        if (order.cancellationRequested || order.stage == Stage.Served) order else order.copy(
            cancellationRequested = true,
            history = order.history + "${LocalDateTime.now().clock()}  Yêu cầu quản lý huỷ đơn"
        )
    }

    internal fun collect(number: Int, transfer: Boolean, reference: String) = change(number) { order ->
        if (order.paid || (transfer && reference.isBlank())) order else {
            val method = if (transfer) "Chuyển khoản QR · ${reference.trim()}" else "Tiền mặt"
            order.copy(paid = true, history = order.history + "${LocalDateTime.now().clock()}  Đã thu tiền · $method")
        }
    }

    internal fun create(lines: List<OrderLine>, customer: String, table: String, note: String, discount: BigDecimal): Int? {
        val state = mutableState.value
        if (lines.isEmpty() || !lines.all { line ->
            line.quantity in 1..99 && state.products.any { it.id == line.product.id && it.available && line.variant in it.variants }
        }) return null
        val now = LocalDateTime.now()
        val order = Order((state.orders.maxOfOrNull { it.number } ?: 0) + 1, lines.toList(),
            customer.trim(), table, now, note = note.trim(), discount = discount.coerceIn(BigDecimal.ZERO, lines.sumOf { it.amount }),
            history = listOf("${now.clock()}  Tạo đơn tại quầy"))
        mutableState.value = state.copy(orders = listOf(order) + state.orders)
        return order.number
    }
}
