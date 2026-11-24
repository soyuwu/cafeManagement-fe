package com.se114.cafe.feature.customer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.se114.cafe.ui.theme.*
import com.se114.cafe.feature.menu.dong

@Composable
fun OrdersScreen(
    activeOrder: ActiveOrder? = null,
    pastOrders: List<PastOrder> = emptyList(),
    onBackClick: () -> Unit = {},
    onCancelOrder: () -> Unit = {}
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CustomerTopBar("Đơn của tôi", onBackClick)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
        ) {
            if (activeOrder == null && pastOrders.isEmpty()) {
                item { Text("Theo dõi đơn hàng chưa được kết nối.", style = MaterialTheme.typography.bodyLarge) }
            }

            // 1. Thẻ đơn hàng hiện tại
            if (activeOrder != null) {
                item {
                    ActiveOrderCard(
                        order = activeOrder,
                        onCancelOrder = onCancelOrder
                    )
                }
            }

            // 2. Tiêu đề mục Đơn trước đó
            if (pastOrders.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Đơn trước đó",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Danh sách đơn cũ
                items(pastOrders, key = { it.orderCode }) { pastOrder ->
                    PastOrderCard(pastOrder = pastOrder)
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
internal fun CustomerTopBar(
    title: String,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(Icons.Default.ChevronLeft, contentDescription = "Quay lại")
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}

@Composable
fun ActiveOrderCard(
    order: ActiveOrder,
    onCancelOrder: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header thông tin đơn
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Mã đơn",
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Normal
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = order.orderCode,
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${order.table} · ${order.orderTime}",
                        style = TextStyle(
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Normal
                        )
                    )
                }

                // Badge trạng thái thanh toán
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF5E3C8)
                ) {
                    Text(
                        text = order.paymentStatus,
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF7A4A0C)
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Dòng thời gian tiến trình (Stepper / Timeline)
            OrderTimeline(steps = order.steps)

            Spacer(modifier = Modifier.height(18.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
            Spacer(modifier = Modifier.height(14.dp))

            // Danh sách các món trong đơn
            order.items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${item.quantity} × ${item.name} (${item.size})",
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Normal
                        )
                    )
                    Text(
                        text = dong(item.price.toBigDecimal()),
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Dòng tổng cộng
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tổng",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Text(
                    text = dong(order.totalPrice.toBigDecimal()),
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Nút huỷ đơn (chỉ khi chưa pha chế)
            if (order.canCancel) {
                OutlinedButton(
                    onClick = onCancelOrder,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.errorContainer),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(
                        text = "Huỷ đơn (chỉ khi chưa pha chế)",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.error
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun OrderTimeline(steps: List<OrderTimelineStep>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        steps.forEachIndexed { index, step ->
            val isLast = index == steps.size - 1

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Cột Node tròn và đường kẻ nối
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(28.dp)
                ) {
                    when (step.status) {
                        OrderStepStatus.COMPLETED -> {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        OrderStepStatus.CURRENT -> {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                            }
                        }
                        OrderStepStatus.UPCOMING -> {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .border(1.5.dp, MaterialTheme.colorScheme.outline, CircleShape)
                            )
                        }
                    }

                    // Đường kẻ dọc nối xuống bước tiếp theo
                    if (!isLast) {
                        val lineColor = if (step.status == OrderStepStatus.COMPLETED) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outline
                        }
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(26.dp)
                                .background(lineColor)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Cột nội dung văn bản của bước
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 1.dp)
                ) {
                    Text(
                        text = step.title,
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = if (step.status == OrderStepStatus.CURRENT || step.status == OrderStepStatus.COMPLETED) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Medium
                            },
                            color = when (step.status) {
                                OrderStepStatus.CURRENT -> MaterialTheme.colorScheme.primary
                                OrderStepStatus.COMPLETED -> MaterialTheme.colorScheme.onSurface
                                OrderStepStatus.UPCOMING -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    )

                    // Subtitle / Nhân viên nhận đơn
                    step.subtitle?.let { sub ->
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = sub,
                            style = TextStyle(
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Normal
                            )
                        )
                    }

                    // Thời gian
                    step.time?.let { timeStr ->
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = timeStr,
                            style = TextStyle(
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Normal
                            )
                        )
                    }

                    if (!isLast) {
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun PastOrderCard(pastOrder: PastOrder) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${pastOrder.orderCode} · ${pastOrder.itemCount} món",
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = pastOrder.dateFormatted,
                    style = TextStyle(
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Normal
                    )
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = dong(pastOrder.totalPrice.toBigDecimal()),
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.End
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = pastOrder.paymentStatus,
                    style = TextStyle(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.End
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun OrdersScreenPreview() {
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
    CafeTheme(CafeRole.Customer) { OrdersScreen(defaultActiveOrder, pastOrders) }
}
