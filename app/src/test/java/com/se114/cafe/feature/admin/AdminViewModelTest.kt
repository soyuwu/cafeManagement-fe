package com.se114.cafe.feature.admin

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class AdminViewModelTest {
    @Test
    fun saveProduct_addsAndUpdatesProduct() {
        val model = AdminViewModel()
        model.save(Product(0, "Espresso", "Cà phê", 28000, "M"))
        val added = model.state.value.products.last()
        model.save(added.copy(status = Availability.Hidden))

        val products = model.state.value.products
        assertEquals(9, products.size)
        assertEquals(Availability.Hidden, products.single { it.id == added.id }.status)
        assertEquals(6, products.count { it.status == Availability.Available })
    }

    @Test
    fun saveEmployee_updatesRoleAndDeleteRemovesEmployee() {
        val model = AdminViewModel()
        model.save(Employee(0, "Lan", "Phục vụ"))
        val added = model.state.value.employees.last()

        model.save(added.copy(role = "Thu ngân"))
        assertEquals("Thu ngân", model.state.value.employees.single { it.id == added.id }.role)

        model.deleteEmployee(added.id)
        assertEquals(6, model.state.value.employees.size)
        assertFalse(model.state.value.employees.any { it.id == added.id })
    }

    @Test
    fun sampleWeek_matchesReferenceTotals() {
        assertEquals("10.770.000đ", money(revenue.takeLast(7).sumOf { it.thousands } * 1000))
        val first = ranking(false).first()
        assertEquals(1_950_000, first.quantity * first.price)
    }
}
