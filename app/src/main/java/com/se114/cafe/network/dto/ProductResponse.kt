package com.se114.cafe.network.dto

import java.math.BigDecimal

data class ProductVariantResponse(
    val id: Long,
    val size: String,
    val price: BigDecimal,
    val isActive: Boolean
)

data class ProductResponse(
    val id: Long?,
    val categoryId: Long?,
    val name: String,
    val description: String?,
    val imageUrl: String?,
    val isAvailable: Boolean,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val variants: List<ProductVariantResponse>? = null,
    val sortOrder: Int = 0,
    val minPrice: BigDecimal? = null
)
