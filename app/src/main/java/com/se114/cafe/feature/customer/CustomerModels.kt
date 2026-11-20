package com.se114.cafe.feature.customer

import androidx.compose.ui.graphics.Color
import com.se114.cafe.ui.theme.DrinkThumbBrown1
import com.se114.cafe.ui.theme.DrinkThumbBrown2
import com.se114.cafe.ui.theme.DrinkThumbBrown3
import com.se114.cafe.ui.theme.DrinkThumbGray

data class CategoryItem(
    val id: String,
    val name: String
)

data class MenuItem(
    val id: Long,
    val name: String,
    val sizes: String = "Size S / M / L",
    val basePrice: Int,
    val isAvailable: Boolean = true,
    val categoryId: String = "coffee",
    val thumbColor: Color = DrinkThumbBrown1
)

data class CartItem(
    val id: Long,
    val productId: Long,
    val name: String,
    val unitPrice: Int,
    val size: String = "M",
    val sugar: String = "50% đường",
    val ice: String = "70% đá",
    val toppings: List<String> = emptyList(),
    val quantity: Int = 1
) {
    val totalPrice: Int
        get() = unitPrice * quantity

    val optionsDescription: String
        get() {
            val parts = mutableListOf<String>()
            if (size.isNotBlank()) parts.add("Size $size")
            if (sugar.isNotBlank()) parts.add(sugar)
            if (ice.isNotBlank()) parts.add(ice)
            return parts.joinToString(" · ")
        }

    val toppingDescription: String?
        get() = if (toppings.isNotEmpty()) "Topping: ${toppings.joinToString(", ")}" else null
}

data class CartSummary(
    val totalQuantity: Int = 3,
    val totalPrice: Int = 87000
)

enum class OrderStepStatus {
    COMPLETED, // Đã xong (icon check xanh)
    CURRENT,   // Đang xử lý (chấm tròn xanh active)
    UPCOMING   // Chưa tới (vòng tròn xám)
}

data class OrderTimelineStep(
    val title: String,
    val subtitle: String? = null,
    val time: String? = null,
    val status: OrderStepStatus
)

data class OrderItemSummary(
    val name: String,
    val quantity: Int,
    val size: String,
    val price: Int
)

data class ActiveOrder(
    val orderCode: String = "#A-0142",
    val table: String = "Bàn T05",
    val orderTime: String = "14:32",
    val paymentStatus: String = "Chưa thanh toán",
    val steps: List<OrderTimelineStep>,
    val items: List<OrderItemSummary>,
    val totalPrice: Int = 139000,
    val canCancel: Boolean = true
)

data class PastOrder(
    val orderCode: String,
    val itemCount: Int,
    val dateFormatted: String,
    val totalPrice: Int,
    val paymentStatus: String = "Đã thanh toán"
)

object MockCustomerData {
    val categories = listOf(
        CategoryItem("coffee", "Cà phê"),
        CategoryItem("tea", "Trà"),
        CategoryItem("smoothie", "Sinh tố"),
        CategoryItem("cake", "Bánh")
    )

    val menuItems = listOf(
        MenuItem(
            id = 1,
            name = "Cà phê sữa đá",
            sizes = "Size S / M / L",
            basePrice = 25000,
            isAvailable = true,
            categoryId = "coffee",
            thumbColor = DrinkThumbBrown1
        ),
        MenuItem(
            id = 2,
            name = "Bạc xỉu",
            sizes = "Size S / M / L",
            basePrice = 29000,
            isAvailable = true,
            categoryId = "coffee",
            thumbColor = DrinkThumbBrown2
        ),
        MenuItem(
            id = 3,
            name = "Cà phê đen",
            sizes = "Size S / M",
            basePrice = 20000,
            isAvailable = true,
            categoryId = "coffee",
            thumbColor = DrinkThumbBrown3
        ),
        MenuItem(
            id = 4,
            name = "Cà phê muối",
            sizes = "Size S / M / L",
            basePrice = 29000,
            isAvailable = false,
            categoryId = "coffee",
            thumbColor = DrinkThumbGray
        )
    )

    val cartItems = listOf(
        CartItem(
            id = 1,
            productId = 1,
            name = "Cà phê sữa đá",
            unitPrice = 35000,
            size = "M",
            sugar = "50% đường",
            ice = "70% đá",
            toppings = listOf("Trân châu"),
            quantity = 1
        ),
        CartItem(
            id = 2,
            productId = 2,
            name = "Bạc xỉu",
            unitPrice = 26000,
            size = "L",
            sugar = "100% đường",
            ice = "Ít đá",
            toppings = emptyList(),
            quantity = 2
        )
    )

    val defaultActiveOrder = ActiveOrder(
        orderCode = "#A-0142",
        table = "Bàn T05",
        orderTime = "14:32",
        paymentStatus = "Chưa thanh toán",
        steps = listOf(
            OrderTimelineStep(
                title = "Đã gửi đơn",
                subtitle = null,
                time = "14:32",
                status = OrderStepStatus.COMPLETED
            ),
            OrderTimelineStep(
                title = "Đang pha chế",
                subtitle = "Nhân viên Giang đã nhận đơn",
                time = null,
                status = OrderStepStatus.CURRENT
            ),
            OrderTimelineStep(
                title = "Sẵn sàng",
                subtitle = null,
                time = null,
                status = OrderStepStatus.UPCOMING
            ),
            OrderTimelineStep(
                title = "Đã phục vụ",
                subtitle = null,
                time = null,
                status = OrderStepStatus.UPCOMING
            )
        ),
        items = listOf(
            OrderItemSummary(
                name = "Cà phê sữa đá",
                quantity = 1,
                size = "M",
                price = 35000
            ),
            OrderItemSummary(
                name = "Bạc xỉu",
                quantity = 2,
                size = "L",
                price = 104000
            )
        ),
        totalPrice = 139000,
        canCancel = true
    )

    val pastOrders = listOf(
        PastOrder(
            orderCode = "#A-0127",
            itemCount = 2,
            dateFormatted = "Hôm qua · 08:15",
            totalPrice = 64000,
            paymentStatus = "Đã thanh toán"
        ),
        PastOrder(
            orderCode = "#A-0098",
            itemCount = 1,
            dateFormatted = "20/10 · 16:40",
            totalPrice = 29000,
            paymentStatus = "Đã thanh toán"
        )
    )
}
