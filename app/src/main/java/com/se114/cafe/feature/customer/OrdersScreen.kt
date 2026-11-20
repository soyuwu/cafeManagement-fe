package com.se114.cafe.feature.customer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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

@Composable
fun OrdersScreen(
    activeOrder: ActiveOrder? = MockCustomerData.defaultActiveOrder,
    pastOrders: List<PastOrder> = MockCustomerData.pastOrders,
    onBackClick: () -> Unit = {},
    onCancelOrder: () -> Unit = {}
) {
    Scaffold(
        containerColor = CafeAppBg,
        topBar = {
            OrdersTopBar(onBackClick = onBackClick)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
        ) {
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
                            color = CafeTextPrimary
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
fun OrdersTopBar(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.ChevronLeft,
            contentDescription = "Quay lại",
            tint = CafeTextPrimary,
            modifier = Modifier
                .size(28.dp)
                .clickable { onBackClick() }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Đơn của tôi",
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = CafeTextPrimary
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
        color = CafeCardBg,
        border = BorderStroke(1.dp, CafeCardBorder),
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
                            color = CafeTextSecondary,
                            fontWeight = FontWeight.Normal
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = order.orderCode,
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = CafeTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${order.table} · ${order.orderTime}",
                        style = TextStyle(
                            fontSize = 13.sp,
                            color = CafeTextSecondary,
                            fontWeight = FontWeight.Normal
                        )
                    )
                }

                // Badge trạng thái thanh toán
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CafeWarningBg
                ) {
                    Text(
                        text = order.paymentStatus,
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CafeWarningText
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Dòng thời gian tiến trình (Stepper / Timeline)
            OrderTimeline(steps = order.steps)

            Spacer(modifier = Modifier.height(18.dp))
            HorizontalDivider(color = Color(0xFFF0F4F2), thickness = 1.dp)
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
                            color = CafeTextPrimary,
                            fontWeight = FontWeight.Normal
                        )
                    )
                    Text(
                        text = formatCurrency(item.price),
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = CafeTextPrimary,
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
                        color = CafeTextPrimary
                    )
                )
                Text(
                    text = formatCurrency(order.totalPrice),
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = CafeTextPrimary
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
                    border = BorderStroke(1.dp, Color(0xFFE2C4C0)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF8F2C22)
                    )
                ) {
                    Text(
                        text = "Huỷ đơn (chỉ khi chưa pha chế)",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF8F2C22)
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
                                    .background(CafeGreenPrimary),
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
                                    .background(CafeGreenLight)
                                    .border(2.dp, CafeGreenPrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(CafeGreenPrimary)
                                )
                            }
                        }
                        OrderStepStatus.UPCOMING -> {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .border(1.5.dp, Color(0xFFCCD5D0), CircleShape)
                            )
                        }
                    }

                    // Đường kẻ dọc nối xuống bước tiếp theo
                    if (!isLast) {
                        val lineColor = if (step.status == OrderStepStatus.COMPLETED) {
                            CafeGreenPrimary
                        } else {
                            Color(0xFFE2E8E5)
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
                                OrderStepStatus.CURRENT -> CafeGreenPrimary
                                OrderStepStatus.COMPLETED -> CafeTextPrimary
                                OrderStepStatus.UPCOMING -> CafeTextSecondary
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
                                color = CafeTextSecondary,
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
                                color = CafeTextSecondary,
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
        color = CafeCardBg,
        border = BorderStroke(1.dp, CafeCardBorder),
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
                        color = CafeTextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = pastOrder.dateFormatted,
                    style = TextStyle(
                        fontSize = 13.sp,
                        color = CafeTextSecondary,
                        fontWeight = FontWeight.Normal
                    )
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatCurrency(pastOrder.totalPrice),
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = CafeTextPrimary,
                        textAlign = TextAlign.End
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = pastOrder.paymentStatus,
                    style = TextStyle(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CafeGreenPrimary,
                        textAlign = TextAlign.End
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OrdersScreenPreview() {
    OrdersScreen()
}
