package com.se114.cafe

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.se114.cafe.feature.customer.CustomerState
import com.se114.cafe.feature.customer.CustomerViewModel
import com.se114.cafe.feature.menu.*
import com.se114.cafe.navigation.AppNavGraphCustomer
import com.se114.cafe.network.dto.ProductVariantResponse
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class CustomerMenuTest {
    @get:Rule val compose = createComposeRule()

    @Test fun customer_searchEditCartAndReturnToMenu() {
        if (android.os.Build.VERSION.SDK_INT >= 37) {
            val instrumentation = InstrumentationRegistry.getInstrumentation()
            instrumentation.uiAutomation.grantRuntimePermission(instrumentation.targetContext.packageName,
                android.Manifest.permission.ACCESS_LOCAL_NETWORK)
        }
        val size = ProductVariantResponse(5, "M", 35000.toBigDecimal(), true)
        val menu = MenuData(listOf(MenuProduct("7", "Latte", "1", listOf(size), minPrice = size.price),
            MenuProduct("8", "Mocha", "1", listOf(size))), listOf(MenuCategory("1", "Cà phê")))
        val model = CustomerViewModel(CustomerState(menu), null)
        compose.setContent { AppNavGraphCustomer(model) }
        compose.onNodeWithText("Tìm món").performTextInput("Lat")
        compose.onNodeWithText("Mocha").assertDoesNotExist()
        compose.onNodeWithText("Latte").performClick()
        compose.onNodeWithText("Cập nhật · 35.000đ").performClick()
        compose.onNodeWithText("Xem giỏ hàng · 1 món").performClick()
        compose.onNodeWithText("+").performClick()
        compose.onNodeWithText("Tạm tính (2 món)").assertIsDisplayed()
        compose.onNodeWithText("Đặt món").assertIsNotEnabled()
        compose.onNodeWithContentDescription("Quay lại").performClick()
        compose.onNodeWithText("Xem giỏ hàng · 2 món").assertIsDisplayed()
        compose.onNodeWithContentDescription("Đơn của tôi").performClick()
        compose.onNodeWithText("Theo dõi đơn hàng chưa được kết nối.").assertIsDisplayed()
        compose.onNodeWithContentDescription("Quay lại").performClick()
        compose.onNodeWithText("Xem giỏ hàng · 2 món").assertIsDisplayed()
    }

    @Test fun menuSync_canRetryAfterNetworkError() {
        var fail = true
        var loaded by mutableStateOf(false)
        compose.setContent {
            val sync = rememberMenuSync({
                if (fail) throw IOException()
                loaded = true
            }, {})
            MenuSyncStatus(sync)
            if (loaded) androidx.compose.material3.Text("Đã tải")
        }
        compose.onNodeWithText("Không đồng bộ được thực đơn").assertIsDisplayed()
        compose.runOnIdle { fail = false }
        compose.onNodeWithText("Thử lại").performClick()
        compose.onNodeWithText("Đã tải").assertIsDisplayed()
    }
}
