package com.se114.cafe.feature.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
internal fun OverviewScreen(
    state: AdminState,
    list: LazyListState,
    header: @Composable () -> Unit,
    openReports: () -> Unit
) {
    LazyColumn(
        state = list,
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { header() }
        item {
            Banner("Doanh thu và top món là số liệu minh họa. Sản phẩm và nhân viên cập nhật trong phiên Admin.", "Chế độ dữ liệu mẫu")
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Statistic("Sản phẩm", state.products.size, Modifier.weight(1f))
                Statistic("Còn bán", state.products.count { it.status == Availability.Available }, Modifier.weight(1f))
                Statistic("Nhân viên", state.employees.size, Modifier.weight(1f))
            }
        }
        item { RevenueTotal(revenue.takeLast(7), "Doanh thu 7 ngày mẫu", openReports) }
        item { RevenueChart(revenue, overview = true) }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Top 3 sản phẩm", style = MaterialTheme.typography.titleLarge)
                Muted("Theo số lượng bán trong 7 ngày mẫu")
            }
        }
        itemsIndexed(ranking(false).take(3)) { index, product -> RankingCard(index + 1, product) }
    }
}

@Composable
internal fun ReportsScreen(list: LazyListState) {
    var showRanking by rememberSaveable { mutableStateOf(false) }
    var twoWeeks by rememberSaveable { mutableStateOf(false) }
    var menu by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val days = if (twoWeeks) revenue else revenue.takeLast(7)
    val products = ranking(twoWeeks)
    val colors = MaterialTheme.colorScheme
    val reset: () -> Unit = { scope.launch { list.scrollToItem(0) } }
    Column(Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Banner("Số liệu minh họa tháng 11/2026, chưa kết nối API.")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Filter("Doanh thu", !showRanking, Modifier.weight(1f)) {
                showRanking = false
                reset()
            }
            Filter("Top sản phẩm", showRanking, Modifier.weight(1f)) {
                showRanking = true
                reset()
            }
            Box(Modifier.weight(1.12f)) {
                Surface(
                    onClick = { menu = true },
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, colors.outline),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(Modifier.heightIn(min = 48.dp).padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(if (twoWeeks) "14 ngày mẫu" else "7 ngày mẫu", Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)
                        Icon(Icons.Outlined.ExpandMore, null, Modifier.size(16.dp))
                    }
                }
                DropdownMenu(expanded = menu, onDismissRequest = { menu = false }, containerColor = colors.surface, tonalElevation = 0.dp, shadowElevation = 0.dp) {
                    listOf(false, true).forEach { option ->
                        DropdownMenuItem(
                            text = { Text(if (option) "14 ngày mẫu" else "7 ngày mẫu") },
                            modifier = if (option == twoWeeks) Modifier.background(colors.secondaryContainer) else Modifier,
                            onClick = {
                                twoWeeks = option
                                menu = false
                                reset()
                            }
                        )
                    }
                }
            }
        }
    }
    HorizontalDivider()
    LazyColumn(state = list, contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        if (showRanking) {
            item {
                Panel {
                    Text("Top sản phẩm theo số lượng bán", style = MaterialTheme.typography.titleMedium)
                    Muted("Trục dọc: số món • Trục ngang: thứ hạng")
                    val step = if (twoWeeks) 32 else 17
                    BarChart(
                        products.mapIndexed { index, product -> Bar("#${index + 1}", product.quantity) },
                        (0..3).map { it * step }, step * 4
                    )
                }
            }
            item { Text("Chi tiết xếp hạng", style = MaterialTheme.typography.titleMedium) }
            itemsIndexed(products) { index, product -> RankingCard(index + 1, product) }
        } else {
            item { RevenueTotal(days, "Tổng doanh thu mẫu") }
            item { RevenueChart(days) }
            item { Text("Chi tiết theo ngày", style = MaterialTheme.typography.titleMedium) }
            items(days, key = { it.day }) { day ->
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(Modifier.fillMaxWidth()) {
                        Text(day.label, Modifier.weight(1f), color = colors.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                        Text(money(day.thousands * 1000), style = MaterialTheme.typography.labelLarge)
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
private fun Statistic(label: String, value: Int, modifier: Modifier) {
    Surface(
        modifier, MaterialTheme.shapes.large, MaterialTheme.colorScheme.surfaceContainer,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Muted(label)
            Text(value.toString(), style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@Composable
private fun RevenueTotal(days: List<DayRevenue>, title: String, open: (() -> Unit)? = null) {
    Panel {
        Text(title, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
        Text(money(days.sumOf { it.thousands } * 1000), style = MaterialTheme.typography.headlineSmall)
        Muted("${days.first().label} – ${days.last().label}/2026")
        if (open != null) TextButton(open) { Text("Xem báo cáo", color = MaterialTheme.colorScheme.secondary) }
    }
}

@Composable
private fun RevenueChart(days: List<DayRevenue>, overview: Boolean = false) {
    Panel(plain = overview) {
        Text("Doanh thu theo ngày", style = MaterialTheme.typography.titleMedium)
        Muted("Trục dọc: nghìn đồng")
        key(days.size) {
            BarChart(days.map { Bar(it.label, it.thousands) }, (0..2200 step 200).toList(), 2200, overview)
        }
        if (days.size > 7) Muted("Vuốt ngang biểu đồ để xem thêm ngày.")
    }
}

@Composable
private fun RankingCard(position: Int, product: Ranking) {
    Panel {
        Text("#$position · ${product.name}", style = MaterialTheme.typography.titleMedium)
        Text("Đã bán: ${product.quantity} món", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("Doanh thu mẫu: ${money(product.quantity * product.price)}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
    }
}

private data class Bar(val label: String, val value: Int)

@Composable
private fun BarChart(bars: List<Bar>, ticks: List<Int>, maximum: Int, latestFirst: Boolean = false) {
    val colors = MaterialTheme.colorScheme
    val style = MaterialTheme.typography.bodySmall.copy(color = colors.onSurfaceVariant)
    val measurer = rememberTextMeasurer()
    val axes = ticks.map { measurer.measure(number(it), style) }
    val labels = bars.map { measurer.measure(it.label, style) }
    val description = bars.joinToString(", ") { "${it.label}: ${number(it.value)}" }
    val axisWidth = axes.maxOf { it.size.width }
    val density = LocalDensity.current
    val axisDp = with(density) { axisWidth.toDp() } + 4.dp
    val minSlot = with(density) { labels.maxOf { it.size.width }.toDp() } + 4.dp
    val scroll = rememberScrollState(if (latestFirst) Int.MAX_VALUE else 0)
    val top = axes.first().size.height / 2f
    BoxWithConstraints(Modifier.fillMaxWidth().semantics { contentDescription = description }) {
        val slot = maxOf(minSlot, (maxWidth - axisDp) / minOf(bars.size, 7))
        Row(Modifier.fillMaxWidth().height(240.dp)) {
            Canvas(Modifier.width(axisDp).fillMaxHeight()) {
                val bottom = size.height - labels.first().size.height - 8.dp.toPx()
                ticks.forEachIndexed { index, tick ->
                    val y = bottom - (bottom - top) * tick / maximum
                    val label = axes[index]
                    drawText(label, topLeft = Offset(axisWidth - label.size.width.toFloat(), y - label.size.height / 2f))
                }
                drawLine(colors.outline, Offset(size.width, top), Offset(size.width, bottom), 1.dp.toPx())
            }
            Row(Modifier.weight(1f).horizontalScroll(scroll)) {
                Canvas(Modifier.width(slot * bars.size).fillMaxHeight()) {
                    val bottom = size.height - labels.first().size.height - 8.dp.toPx()
                    val height = bottom - top
                    ticks.forEach { tick ->
                        val y = bottom - height * tick / maximum
                        drawLine(colors.outline, Offset(0f, y), Offset(size.width, y), 1.dp.toPx())
                    }
                    val slotPx = slot.toPx()
                    bars.forEachIndexed { index, bar ->
                        val center = slotPx * (index + 0.5f)
                        val barHeight = height * bar.value / maximum
                        drawRect(colors.secondary, Offset(center - slotPx / 4, bottom - barHeight), Size(slotPx / 2, barHeight))
                        val label = labels[index]
                        drawText(label, topLeft = Offset(center - label.size.width / 2f, bottom + 6.dp.toPx()))
                    }
                }
            }
        }
    }
}
