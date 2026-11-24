package com.se114.cafe.feature.customer

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
    val orderCode: String,
    val table: String,
    val orderTime: String,
    val paymentStatus: String,
    val steps: List<OrderTimelineStep>,
    val items: List<OrderItemSummary>,
    val totalPrice: Int,
    val canCancel: Boolean
)

data class PastOrder(
    val orderCode: String,
    val itemCount: Int,
    val dateFormatted: String,
    val totalPrice: Int,
    val paymentStatus: String = "Đã thanh toán"
)
