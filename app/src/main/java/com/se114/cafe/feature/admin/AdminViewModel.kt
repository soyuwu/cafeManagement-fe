package com.se114.cafe.feature.admin

import androidx.lifecycle.ViewModel
import java.text.NumberFormat
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal enum class Availability(val label: String) {
    Available("Còn bán"), SoldOut("Tạm hết"), Hidden("Đã ẩn")
}

internal data class Product(
    val id: Int, val name: String, val category: String, val price: Int,
    val sizes: String, val status: Availability = Availability.Available
)

internal data class Employee(val id: Int, val name: String, val role: String)

internal data class AdminState(
    val products: List<Product> = listOf(
        Product(1, "Cà phê đen", "Cà phê", 25000, "M, L"),
        Product(2, "Cà phê sữa", "Cà phê", 30000, "M, L"),
        Product(3, "Bạc xỉu", "Cà phê", 35000, "M, L"),
        Product(4, "Latte", "Cà phê", 45000, "M, L", Availability.SoldOut),
        Product(5, "Trà đào cam sả", "Trà trái cây", 45000, "S, M, L"),
        Product(6, "Trà vải", "Trà trái cây", 40000, "M, L"),
        Product(7, "Matcha latte", "Matcha", 55000, "M, L"),
        Product(8, "Cacao nóng", "Cacao", 40000, "M", Availability.Hidden)
    ),
    val employees: List<Employee> = listOf(
        Employee(1, "Nguyễn Minh Anh", "Thu ngân"),
        Employee(2, "Trần Hoàng Nam", "Pha chế"),
        Employee(3, "Lê Ngọc Hà", "Phục vụ"),
        Employee(4, "Phạm Quốc Bảo", "Pha chế"),
        Employee(5, "Võ Thanh Trúc", "Thu ngân"),
        Employee(6, "Đỗ Gia Huy", "Phục vụ")
    )
)

internal class AdminViewModel : ViewModel() {
    private val mutableState = MutableStateFlow(AdminState())
    val state = mutableState.asStateFlow()

    fun save(product: Product) {
        mutableState.update { state ->
            val products = state.products
            state.copy(products = if (product.id == 0) {
                products + product.copy(id = (products.maxOfOrNull { it.id } ?: 0) + 1)
            } else {
                products.map { if (it.id == product.id) product else it }
            })
        }
    }

    fun save(employee: Employee) {
        mutableState.update { state ->
            val employees = state.employees
            state.copy(employees = if (employee.id == 0) {
                employees + employee.copy(id = (employees.maxOfOrNull { it.id } ?: 0) + 1)
            } else {
                employees.map { if (it.id == employee.id) employee else it }
            })
        }
    }

    fun deleteEmployee(id: Int) {
        mutableState.update { it.copy(employees = it.employees.filterNot { employee -> employee.id == id }) }
    }
}

internal data class DayRevenue(val day: Int, val thousands: Int) {
    val label get() = "${day.toString().padStart(2, '0')}/11"
}

internal data class Ranking(val name: String, val quantity: Int, val price: Int)

internal val revenue = listOf(1040, 1260, 910, 1380, 1520, 1840, 1690, 1250, 980, 1470, 1320, 1860, 2150, 1740)
    .mapIndexed { index, value -> DayRevenue(index + 1, value) }

internal fun ranking(twoWeeks: Boolean) = listOf(
    Ranking("Cà phê sữa", if (twoWeeks) 122 else 65, 30000),
    Ranking("Cà phê đen", if (twoWeeks) 107 else 54, 25000),
    Ranking("Trà đào cam sả", if (twoWeeks) 82 else 42, 45000),
    Ranking("Bạc xỉu", if (twoWeeks) 73 else 36, 35000),
    Ranking("Latte", if (twoWeeks) 57 else 28, 45000)
)

internal fun number(value: Int): String = NumberFormat.getIntegerInstance(Locale.forLanguageTag("vi-VN")).format(value)
internal fun money(value: Int): String = "${number(value)}đ"
