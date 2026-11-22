package com.se114.cafe

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class StaffMenuTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun counterOrder_selectSizeAndCollectCash() {
        compose.onNodeWithText("Tạo đơn tại quầy", substring = true).performClick()
        compose.onNodeWithText("Cà phê muối").assertIsNotEnabled()
        compose.onNodeWithText("Cà phê sữa đá").performClick()
        compose.onNodeWithText("M", substring = false).performClick()
        compose.onNodeWithText("Cập nhật · 35.000đ").performClick()
        compose.onNodeWithText("1 × Cà phê sữa đá (M)").assertIsDisplayed()
        compose.onNodeWithText("Tạo đơn & thu tiền").performClick()
        compose.onNodeWithText("Xác nhận đã thu 35.000đ").performScrollTo().performClick()
        compose.onNodeWithText("Đã thanh toán").assertExists()
        compose.onNodeWithContentDescription("Quay lại").performClick()
        compose.onNodeWithText("#A-0143").assertIsDisplayed()
    }
}
