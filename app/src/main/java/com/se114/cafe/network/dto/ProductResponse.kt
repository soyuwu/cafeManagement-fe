package com.se114.cafe.network.dto

data class ProductResponse(
    val id: Long?,
    val categoryId: Long?,
    val name: String,
    val description: String?,
    val imageUrl: String?,
    val isAvailable: Boolean,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String
)
