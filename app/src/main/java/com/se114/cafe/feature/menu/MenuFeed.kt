package com.se114.cafe.feature.menu

import com.se114.cafe.repository.MenuRepository

internal data class MenuData(
    val products: List<MenuProduct> = emptyList(),
    val categories: List<MenuCategory> = emptyList()
)

internal class MenuFeed(private val repository: MenuRepository) {
    private var initialized = false

    suspend fun snapshot(): MenuData? {
        val result = repository.snapshot()
        if (result.notModified && initialized) return null
        val categories = result.body.categories.mapNotNull { category ->
            category.id?.let { MenuCategory(it.toString(), category.name) }
        }
        val categoryIds = categories.map { it.id }.toSet()
        val products = result.body.products.mapNotNull { product ->
            val id = product.id ?: return@mapNotNull null
            val category = product.categoryId?.toString()?.takeIf { it in categoryIds } ?: "other"
            MenuProduct(id.toString(), product.name, category, product.variants.orEmpty().filter { it.isActive },
                product.isAvailable, product.minPrice)
        }
        val tabs = if (products.any { it.categoryId == "other" }) categories + MenuCategory("other", "Khác") else categories
        initialized = true
        return MenuData(products, tabs)
    }

    suspend fun availability(current: MenuData): MenuData? {
        val available = repository.availability().associate { it.productId.toString() to it.isAvailable }
        if (available.keys != current.products.map { it.id }.toSet()) return snapshot()
        return current.copy(products = current.products.map { it.copy(available = available.getValue(it.id)) })
    }
}
