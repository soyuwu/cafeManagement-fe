package com.se114.cafe.network.dto

data class ProductRequest(
    val categoryId: Long,
    val name: String,
    val description: String? = null,
    val imageUrl: String? = null,
    val isAvailable: Boolean = true,
    val isActive: Boolean = true
)

data class ProductAvailabilityRequest(val isAvailable: Boolean)
