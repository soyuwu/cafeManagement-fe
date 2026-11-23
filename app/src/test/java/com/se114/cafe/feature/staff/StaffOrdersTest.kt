package com.se114.cafe.feature.staff

import com.se114.cafe.network.dto.ProductVariantResponse
import com.se114.cafe.feature.menu.MenuProduct
import org.junit.Assert.*
import org.junit.Test

class StaffOrdersTest {
    private val product = MenuProduct("coffee", "Coffee", "Coffee", listOf(ProductVariantResponse(2, "M", 35000.toBigDecimal(), true)))
    private val model = StaffOrdersViewModel(listOf(product))

    @Test fun draft_retainsUnavailableSelectionsAndUsesUpdatedPrices() {
        val draft = DraftItem(product.id, 2, product.name, "M", 30000.toBigDecimal(), 2)
        val row = draft.resolve(product)
        assertTrue(row.priceChanged)
        assertEquals(70000.toBigDecimal(), row.amount)
        assertNull(row.issue)
        assertEquals("Tạm hết", draft.resolve(product.copy(available = false)).issue)
        assertEquals("Kích cỡ đã ngừng bán", draft.resolve(product.copy(variants = emptyList())).issue)
        assertEquals(draft, draft.resolve(null).item)
        assertNull(draft.resolve(null).line)
    }

    @Test fun newShift_startsEmpty() {
        val state = StaffOrdersViewModel().state.value
        assertTrue(state.products.isEmpty())
        assertTrue(state.orders.isEmpty())
    }

    @Test fun counterOrder_usesSelectedSizeAndDiscount() {
        val number = model.create(listOf(OrderLine(product, product.variants.single(), 2)), "Lan", "Bàn T01", "ít đá", 5000.toBigDecimal())
        val order = model.state.value.orders.first()
        assertEquals(number, order.number)
        assertEquals(65000.toBigDecimal(), order.total)
        assertFalse(order.paid)
    }

    @Test fun payment_requiresTransferReferenceAndPreservesPreparationStage() {
        model.create(listOf(OrderLine(product, product.variants.single(), 1)), "", "", "", 0.toBigDecimal())
        val order = model.state.value.orders.first()
        model.collect(order.number, true, " ")
        assertFalse(model.state.value.orders.first().paid)
        model.collect(order.number, true, "REF123")
        model.collect(order.number, false, "")
        val paid = model.state.value.orders.first()
        assertTrue(paid.paid)
        assertEquals(order.stage, paid.stage)
        assertEquals(order.history.size + 1, paid.history.size)
        assertTrue(paid.history.last().contains("REF123"))
    }

    @Test fun repeatedAccept_advancesOnlyOnce() {
        model.create(listOf(OrderLine(product, product.variants.single(), 1)), "", "", "", 0.toBigDecimal())
        val order = model.state.value.orders.first()
        repeat(2) { model.advance(order.number, Stage.Pending) }
        assertEquals(Stage.Preparing, model.state.value.orders.first().stage)
        assertFalse(model.state.value.orders.first().paid)
    }
}
