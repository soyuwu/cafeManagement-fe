package com.se114.cafe.feature.staff

import androidx.lifecycle.ViewModel
import com.se114.cafe.feature.menu.MenuProduct
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

internal data class OrderLine(val product: MenuProduct, val size: String, val quantity: Int, val options: String = "") {
    val amount get() = product.prices.getValue(size) * quantity
    val label get() = "$quantity × ${product.name}" + if (size.isEmpty()) "" else " ($size)"
}
internal data class Order(
    val number: Int, val lines: List<OrderLine>, val customer: String, val table: String,
    val placed: LocalDateTime, val fromApp: Boolean = false, val stage: Stage = Stage.Pending,
    val paid: Boolean = false, val note: String = "", val discount: Int = 0,
    val history: List<String> = emptyList(), val cancellationRequested: Boolean = false
) {
    val code get() = "#A-" + number.toString().padStart(4, '0')
    val total get() = (lines.sumOf { it.amount } - discount).coerceAtLeast(0)
    val location get() = listOf(table.ifBlank { "Mang đi" }, customer.ifBlank { "Khách vãng lai" }).joinToString(" · ")
}
internal fun LocalDateTime.clock(): String = format(DateTimeFormatter.ofPattern("HH:mm"))

internal data class StaffState(val products: List<MenuProduct>, val orders: List<Order>)

class StaffOrdersViewModel : ViewModel() {
    private val mutableState = MutableStateFlow(sampleShift())
    internal val state = mutableState.asStateFlow()

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

    internal fun create(lines: List<OrderLine>, customer: String, table: String, note: String, discount: Int): Int {
        val state = mutableState.value
        require(lines.isNotEmpty() && lines.all { line ->
            line.quantity in 1..99 && state.products.any { it.id == line.product.id && it.available }
        })
        val now = LocalDateTime.now()
        val order = Order((state.orders.maxOfOrNull { it.number } ?: 0) + 1, lines.toList(),
            customer.trim(), table, now, note = note.trim(), discount = discount.coerceIn(0, lines.sumOf { it.amount }),
            history = listOf("${now.clock()}  Tạo đơn tại quầy"))
        mutableState.value = state.copy(orders = listOf(order) + state.orders)
        return order.number
    }
}

private fun sampleShift(): StaffState {
    val products = listOf(
        MenuProduct("milk", "Cà phê sữa đá", "Cà phê", mapOf("S" to 25000, "M" to 35000, "L" to 40000)),
        MenuProduct("white", "Bạc xỉu", "Cà phê", mapOf("S" to 29000, "M" to 39000, "L" to 52000)),
        MenuProduct("black", "Cà phê đen", "Cà phê", mapOf("S" to 20000, "M" to 22000, "L" to 27000)),
        MenuProduct("salt", "Cà phê muối", "Cà phê", mapOf("M" to 35000), false),
        MenuProduct("peach", "Trà đào cam sả", "Trà", mapOf("M" to 35000, "L" to 40000)),
        MenuProduct("mango", "Sinh tố xoài", "Sinh tố", mapOf("M" to 40000)),
        MenuProduct("cake", "Bánh chuối", "Bánh", mapOf("" to 25000))
    )
    val now = LocalDateTime.now()
    val orders = listOf(Stage.Pending, Stage.Pending, Stage.Pending, Stage.Preparing, Stage.Preparing, Stage.Ready)
        .mapIndexed { index, stage ->
            val placed = now.minusMinutes(2L + 3 * index)
            val lines = when (index % 3) {
                0 -> listOf(OrderLine(products[0], "M", 1, "50% đường · 70% đá · Trân châu"),
                    OrderLine(products[1], "L", 2, "100% đường · Ít đá"))
                1 -> listOf(OrderLine(products[2], "M", 2))
                else -> listOf(OrderLine(products[4], "L", 1))
            }
            val guest = index % 3 == 1
            val events = mutableListOf("${placed.clock()}  Tạo đơn")
            if (stage != Stage.Pending) events += "${placed.plusMinutes(1).clock()}  Đang pha"
            if (stage == Stage.Ready) events += "${placed.plusMinutes(2).clock()}  Sẵn sàng"
            Order(142 - index, lines, if (guest) "" else "Gia Huy", if (guest) "" else if (index == 0) "Bàn T05" else "Bàn T02",
                placed, !guest, stage, paid = guest, note = if (index == 0) "mang ra cùng lúc" else "", history = events)
        }
    return StaffState(products, orders)
}
