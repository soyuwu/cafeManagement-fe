package com.se114.cafe.feature.staff

import org.junit.Assert.*
import org.junit.Test

class StaffOrdersTest {
    private val model = StaffOrdersViewModel()

    @Test fun counterOrder_usesSelectedSizeAndDiscount() {
        val product = model.state.value.products.first()
        val number = model.create(listOf(OrderLine(product, "M", 2)), "Lan", "Bàn T01", "ít đá", 5000)
        val order = model.state.value.orders.first()
        assertEquals(number, order.number)
        assertEquals(65000, order.total)
        assertFalse(order.paid)
    }

    @Test fun payment_requiresTransferReferenceAndPreservesPreparationStage() {
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
        val order = model.state.value.orders.first()
        repeat(2) { model.advance(order.number, Stage.Pending) }
        assertEquals(Stage.Preparing, model.state.value.orders.first().stage)
        assertFalse(model.state.value.orders.first().paid)
    }
}
