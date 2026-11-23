package com.se114.cafe

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import com.se114.cafe.feature.menu.MenuCategory
import com.se114.cafe.feature.staff.CounterOrderScreen
import androidx.compose.ui.test.junit4.createComposeRule
import com.se114.cafe.network.dto.ProductVariantResponse
import com.se114.cafe.feature.menu.MenuProduct
import com.se114.cafe.feature.staff.StaffOrdersViewModel
import com.se114.cafe.navigation.AppNavGraph
import com.se114.cafe.ui.theme.CafeTheme
import org.junit.Rule
import org.junit.Test

class StaffMenuTest {
    @get:Rule val compose = createComposeRule()

    @Test fun counterOrder_repairsRemovedSizeAndShowsUpdatedTotal() {
        val medium = ProductVariantResponse(4, "M", 30000.toBigDecimal(), true)
        val large = ProductVariantResponse(5, "L", 40000.toBigDecimal(), true)
        val coffee = MenuProduct("7", "Latte", "1", listOf(medium, large), minPrice = medium.price)
        val products = mutableStateOf(listOf(coffee))
        compose.setContent {
            CafeTheme {
                CounterOrderScreen(products.value, listOf(MenuCategory("1", "Cà phê"), MenuCategory("2", "Trà")),
                    {}, {}, {}) { _, _, _, _, _ -> }
            }
        }
        compose.onNodeWithText("Trà").performClick()
        compose.onNodeWithText("Chưa có món trong danh mục này").assertIsDisplayed()
        compose.onNodeWithText("Cà phê").performClick()
        compose.onNodeWithText("Từ 30.000đ").performClick()
        compose.onNodeWithText("Cập nhật · 30.000đ").performClick()
        compose.runOnIdle { products.value = listOf(coffee.copy(variants = listOf(large), minPrice = large.price)) }
        compose.onNodeWithText("Tạo đơn & thu tiền").assertIsNotEnabled()
        compose.onNodeWithText("Đổi kích cỡ").performScrollTo().performClick()
        compose.onNodeWithText("Cập nhật · 40.000đ").performClick()
        compose.onNodeWithText("1 × Latte (L)").assertIsDisplayed()
        compose.onNodeWithText("Tạo đơn & thu tiền").assertIsEnabled()
        val repriced = large.copy(price = 45000.toBigDecimal())
        compose.runOnIdle { products.value = listOf(coffee.copy(variants = listOf(repriced), minPrice = repriced.price)) }
        compose.onNodeWithText("Đơn giá đã cập nhật").assertIsDisplayed()
    }

    @Test fun counterOrder_selectSizeAndCollectCash() {
        if (android.os.Build.VERSION.SDK_INT >= 37) {
            val instrumentation = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation()
            instrumentation.uiAutomation.grantRuntimePermission(instrumentation.targetContext.packageName,
                android.Manifest.permission.ACCESS_LOCAL_NETWORK)
        }
        val model = StaffOrdersViewModel(listOf(
            MenuProduct("milk", "Cà phê sữa đá", "Cà phê", listOf(ProductVariantResponse(2, "M", 35000.toBigDecimal(), true),
                ProductVariantResponse(1, "S", 40000.toBigDecimal(), true)), minPrice = 35000.toBigDecimal()),
            MenuProduct("salt", "Cà phê muối", "Cà phê", listOf(ProductVariantResponse(2, "M", 35000.toBigDecimal(), true)), available = false, minPrice = 35000.toBigDecimal()),
            MenuProduct("empty", "Trà", "Trà", emptyList())
        ))
        compose.setContent { CafeTheme { AppNavGraph(model) } }
        compose.onNodeWithText("Tạo đơn tại quầy", substring = true).performClick()
        compose.onNodeWithText("Cà phê muối").performClick()
        compose.onNodeWithText("M · 35.000đ").assertIsDisplayed()
        compose.onNodeWithText("Cập nhật", substring = true).assertDoesNotExist()
        compose.onNodeWithText("Đóng").performClick()
        compose.onAllNodesWithText("Từ 35.000đ").assertCountEquals(2)
        compose.onNodeWithText("Trà").performClick()
        compose.onNodeWithText("Chưa có kích cỡ").assertIsNotEnabled()
        compose.onNodeWithText("Cà phê").performClick()
        compose.onNodeWithText("Cà phê sữa đá").performClick()
        compose.onNodeWithText("M · 35.000đ").assertIsSelected()
        compose.onNodeWithText("S · 40.000đ").performClick()
        compose.onNodeWithText("M · 35.000đ").performClick()
        compose.onNodeWithText("Cập nhật · 35.000đ").performClick()
        compose.onNodeWithText("1 × Cà phê sữa đá (M)").assertIsDisplayed()
        compose.onNodeWithText("Tạo đơn & thu tiền").performClick()
        compose.onNodeWithText("Xác nhận đã thu 35.000đ").performScrollTo().performClick()
        compose.onNodeWithText("Đã thanh toán").assertExists()
        compose.onNodeWithContentDescription("Quay lại").performClick()
        compose.onNodeWithText("#A-0001").assertIsDisplayed()
    }
}
