package com.se114.cafe.feature.staff

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.se114.cafe.R

@Composable
internal fun StaffHeader(title: String, back: () -> Unit, trailing: @Composable () -> Unit = {}) {
    Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary).padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        IconButton(onClick = back) { Icon(painterResource(R.drawable.ic_back), "Quay lại", tint = Color.White) }
        Text(title, Modifier.weight(1f), style = MaterialTheme.typography.titleLarge, color = Color.White)
        trailing()
    }
}

@Composable
internal fun OrderPanel(modifier: Modifier = Modifier, emphasized: Boolean = false, content: @Composable ColumnScope.() -> Unit) {
    val colors = MaterialTheme.colorScheme
    Surface(modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large,
        border = BorderStroke(if (emphasized) 2.dp else 1.dp, if (emphasized) colors.secondary else colors.outline)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp), content = content)
    }
}

@Composable
internal fun OrderAction(label: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    Button(onClick, modifier.fillMaxWidth().heightIn(min = 48.dp), enabled = enabled, shape = MaterialTheme.shapes.medium) { Text(label) }
}

@Composable
internal fun Tag(label: String, ink: Color, fill: Color) {
    Surface(shape = MaterialTheme.shapes.small, color = fill, contentColor = ink) {
        Text(label, Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
internal fun PaymentTag(paid: Boolean) {
    if (paid) Tag("Đã thanh toán", Color(0xFF2F5D3A), Color(0xFFDDEBDD))
    else Tag("Chưa thanh toán", Color(0xFF7A4A0C), Color(0xFFF5E3C8))
}

@Composable
internal fun PriceRow(label: String, amount: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, Modifier.weight(1f))
        Text(amount, style = MaterialTheme.typography.titleMedium)
    }
}
