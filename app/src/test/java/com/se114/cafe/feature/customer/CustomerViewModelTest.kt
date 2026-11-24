package com.se114.cafe.feature.customer

import com.se114.cafe.feature.menu.MenuData
import com.se114.cafe.feature.menu.MenuProduct
import com.se114.cafe.network.dto.ProductVariantResponse
import org.junit.Assert.*
import org.junit.Test

class CustomerViewModelTest {
    private val small = ProductVariantResponse(5, "S", 25000.toBigDecimal(), true)
    private val large = ProductVariantResponse(6, "L", 35000.toBigDecimal(), true)
    private val product = MenuProduct("7", "Latte", "1", listOf(small, large))
    private val menu = MenuData(listOf(product))

    @Test fun cart_keepsSizesSeparateAndRemovesZeroQuantity() {
        val model = CustomerViewModel(CustomerState(menu), null)
        model.setQuantity("7", small.id, 1)
        model.setQuantity("7", large.id, 2)
        model.setQuantity("7", small.id, 3)
        assertEquals(5, model.state.value.quantity)
        assertEquals(145000.toBigDecimal(), model.state.value.total)
        model.setQuantity("7", large.id, 0)
        assertEquals(small.id, model.state.value.rows.single().item.variantId)
        assertEquals(75000.toBigDecimal(), model.state.value.total)
    }

    @Test fun cart_repricesAndKeepsUnavailableRowsRemovable() {
        val model = CustomerViewModel(CustomerState(menu), null)
        model.setQuantity("7", small.id, 2)
        val repriced = small.copy(price = 28000.toBigDecimal())
        val changed = model.state.value.copy(menu = MenuData(listOf(product.copy(variants = listOf(repriced)))))
        assertTrue(changed.rows.single().priceChanged)
        assertEquals(56000.toBigDecimal(), changed.total)
        val missing = changed.copy(menu = MenuData(listOf(product.copy(variants = listOf(large)))))
        assertEquals("Kích cỡ đã ngừng bán", missing.rows.single().issue)
        val soldOut = CustomerViewModel(changed.copy(menu = MenuData(listOf(product.copy(available = false)))), null)
        soldOut.setQuantity("7", small.id, 3)
        assertEquals(2, soldOut.state.value.quantity)
        assertEquals("Tạm hết", soldOut.state.value.rows.single().issue)
        soldOut.setQuantity("7", small.id, 0)
        assertTrue(soldOut.state.value.cart.isEmpty())
    }
}
