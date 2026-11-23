package com.se114.cafe.feature.staff

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.se114.cafe.feature.menu.dong

@Composable
internal fun OrderDetailScreen(order: Order, back: () -> Unit, advance: () -> Unit, cancel: () -> Unit,
    collect: (Boolean, String) -> Unit) {
    var transfer by rememberSaveable(order.number) { mutableStateOf(false) }
    var reference by rememberSaveable(order.number) { mutableStateOf("") }
    var confirm by rememberSaveable { mutableStateOf(false) }
    val colors = MaterialTheme.colorScheme
    Column(Modifier.fillMaxSize().background(colors.background)) {
        StaffHeader("Đơn ${order.code}", back) {
            when (order.stage) {
                Stage.Pending -> Tag(order.stage.label, Color(0xFF7A4A0C), Color(0xFFF5E3C8))
                Stage.Preparing -> Tag(order.stage.label, Color(0xFF33506B), Color(0xFFDCE6EE))
                else -> Tag(order.stage.label, Color(0xFF2F5D3A), Color(0xFFDDEBDD))
            }
        }
        Column(Modifier.verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OrderPanel {
                PriceRow(order.location + if (order.fromApp) " (app khách)" else " (quầy)", order.placed.clock())
                order.lines.forEach { line ->
                    PriceRow(line.label, dong(line.amount))
                    if (line.options.isNotEmpty()) Text(line.options, color = colors.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                }
                HorizontalDivider()
                if (order.note.isNotEmpty()) Text("Ghi chú: ${order.note}", style = MaterialTheme.typography.bodyMedium)
                if (order.discount.signum() > 0) PriceRow("Giảm giá", "−" + dong(order.discount))
                PriceRow("Tổng", dong(order.total))
            }
            if (order.stage != Stage.Served) Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                OrderAction(order.stage.action, advance, Modifier.weight(1.6f))
                OutlinedButton(onClick = { confirm = true }, enabled = !order.cancellationRequested, modifier = Modifier.weight(1f),
                    border = BorderStroke(1.dp, colors.error), shape = MaterialTheme.shapes.medium) {
                    Text(if (order.cancellationRequested) "Đã yêu cầu huỷ" else "Huỷ đơn", color = colors.error)
                }
            }
            OrderPanel {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Thanh toán", Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
                    PaymentTag(order.paid)
                }
                if (!order.paid) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(false, true).forEach { qr ->
                            FilterChip(selected = qr == transfer, onClick = { transfer = qr }, modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                                label = { Text(if (qr) "Chuyển khoản QR" else "Tiền mặt") })
                        }
                    }
                    if (transfer) {
                        Text("Mã giao dịch (khi chuyển khoản)", style = MaterialTheme.typography.labelLarge)
                        Entry(reference, { reference = it }, "reference_code", Modifier.fillMaxWidth())
                    }
                    OrderAction("Xác nhận đã thu " + dong(order.total), { collect(transfer, reference) }, enabled = !transfer || reference.isNotBlank())
                }
            }
            Text("Lịch sử", style = MaterialTheme.typography.titleMedium)
            order.history.forEach { Text(it, style = MaterialTheme.typography.bodyMedium, color = colors.onSurfaceVariant) }
        }
    }
    if (confirm) AlertDialog(onDismissRequest = { confirm = false }, title = { Text("Yêu cầu huỷ đơn?") },
        text = { Text("Đơn cần quản lý phê duyệt.") }, containerColor = colors.surface,
        confirmButton = { TextButton(onClick = { cancel(); confirm = false }) { Text("Yêu cầu huỷ") } },
        dismissButton = { TextButton(onClick = { confirm = false }) { Text("Quay lại") } })
}
