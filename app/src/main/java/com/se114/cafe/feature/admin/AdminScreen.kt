package com.se114.cafe.feature.admin

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.se114.cafe.ui.theme.CafeRole
import com.se114.cafe.ui.theme.CafeTheme
import kotlinx.coroutines.launch

private enum class Tab(val label: String, val icon: ImageVector) {
    Overview("Tổng quan", Icons.Outlined.Dashboard),
    Products("Sản phẩm", Icons.Outlined.Inventory2),
    Employees("Nhân viên", Icons.Outlined.Badge),
    Reports("Báo cáo", Icons.Outlined.BarChart)
}

@Composable
internal fun AdminScreen(model: AdminViewModel = viewModel()) {
    val state by model.state.collectAsState()
    AdminContent(state, Tab.Overview, model::save, model::save, model::deleteEmployee)
}

@Composable
private fun AdminContent(
    state: AdminState,
    initialTab: Tab,
    saveProduct: (Product) -> Unit,
    saveEmployee: (Employee) -> Unit,
    deleteEmployee: (Int) -> Unit
) {
    var tab by rememberSaveable { mutableStateOf(initialTab) }
    val holder = rememberSaveableStateHolder()
    BackHandler(tab != Tab.Overview) { tab = Tab.Overview }
    Scaffold(contentWindowInsets = WindowInsets(0, 0, 0, 0), bottomBar = {
        Column {
            HorizontalDivider()
            Row(Modifier.padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Tab.entries.forEach { item ->
                    val active = item == tab
                    Surface(
                        onClick = { tab = item },
                        modifier = Modifier.weight(1f).semantics {
                            selected = active
                            role = Role.Tab
                        },
                        shape = MaterialTheme.shapes.small,
                        color = if (active) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface,
                        contentColor = if (active) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                    ) {
                        Column(
                            Modifier.heightIn(min = 64.dp).padding(vertical = 8.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(item.icon, null)
                            Text(item.label, style = MaterialTheme.typography.labelMedium, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            holder.SaveableStateProvider(tab.name) {
                val list = rememberLazyListState()
                val scope = rememberCoroutineScope()
                var refresh by rememberSaveable { mutableIntStateOf(0) }
                val overview = tab == Tab.Overview
                val header: @Composable () -> Unit = {
                    Row(
                        Modifier.fillMaxWidth()
                            .padding(horizontal = if (overview) 0.dp else 16.dp)
                            .padding(top = 16.dp, bottom = if (overview) 0.dp else 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Muted("CAFE ADMIN")
                            Text(tab.label, style = MaterialTheme.typography.headlineSmall)
                        }
                        TextButton(onClick = {
                            refresh++
                            scope.launch { list.scrollToItem(0) }
                        }) { Text("Tải lại", color = MaterialTheme.colorScheme.secondary) }
                    }
                }
                if (!overview) header()
                key(refresh) {
                    when (tab) {
                        Tab.Overview -> OverviewScreen(state, list, header) { tab = Tab.Reports }
                        Tab.Products -> ProductsScreen(state.products, list, saveProduct)
                        Tab.Employees -> EmployeesScreen(state.employees, list, saveEmployee, deleteEmployee)
                        Tab.Reports -> ReportsScreen(list)
                    }
                }
            }
        }
    }
}

@Preview(name = "Tổng quan", group = "Admin", widthDp = 393, heightDp = 850, showBackground = true)
@Composable
private fun OverviewScreenPreview() {
    AdminPreview(Tab.Overview)
}

@Preview(name = "Sản phẩm", group = "Admin", widthDp = 393, heightDp = 850, showBackground = true)
@Composable
private fun ProductsScreenPreview() {
    AdminPreview(Tab.Products)
}

@Preview(name = "Nhân viên", group = "Admin", widthDp = 393, heightDp = 850, showBackground = true)
@Composable
private fun EmployeesScreenPreview() {
    AdminPreview(Tab.Employees)
}

@Preview(name = "Báo cáo", group = "Admin", widthDp = 393, heightDp = 850, showBackground = true)
@Composable
private fun ReportsScreenPreview() {
    AdminPreview(Tab.Reports)
}

@Composable
private fun AdminPreview(initialTab: Tab) {
    CafeTheme(CafeRole.Admin) {
        AdminContent(
            state = remember { AdminState() },
            initialTab = initialTab,
            saveProduct = {},
            saveEmployee = {},
            deleteEmployee = {}
        )
    }
}

@Composable
internal fun Banner(message: String, title: String = "Dữ liệu mẫu") {
    Surface(Modifier.fillMaxWidth(), MaterialTheme.shapes.small, MaterialTheme.colorScheme.secondaryContainer) {
        Column(Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.labelLarge)
            Muted(message)
        }
    }
}

@Composable
internal fun Muted(value: String) {
    Text(value, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
}

@Composable
internal fun Panel(
    modifier: Modifier = Modifier.fillMaxWidth(),
    active: Boolean = false,
    plain: Boolean = false,
    click: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val shape = MaterialTheme.shapes.large
    val fill = when {
        active -> colors.secondaryContainer
        plain -> colors.surface
        else -> colors.surfaceContainer
    }
    val border = BorderStroke(1.dp, if (active) colors.onSurface else colors.outline)
    val body = @Composable {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp), content = content)
    }
    if (click == null) {
        Surface(modifier, shape, fill, border = border, content = body)
    } else {
        Surface(click, modifier.semantics { selected = active }, shape = shape, color = fill, border = border, content = body)
    }
}

@Composable
internal fun Filter(label: String, active: Boolean, modifier: Modifier = Modifier, click: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Surface(
        click, modifier.semantics { selected = active }, shape = MaterialTheme.shapes.small,
        color = if (active) colors.secondaryContainer else colors.surface,
        border = BorderStroke(1.dp, if (active) colors.onSurface else colors.outline)
    ) {
        Box(Modifier.heightIn(min = 48.dp).padding(6.dp), contentAlignment = Alignment.Center) {
            Text(label, style = MaterialTheme.typography.labelMedium, textAlign = TextAlign.Center)
        }
    }
}
