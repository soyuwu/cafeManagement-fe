package com.se114.cafe.network.dto

data class CategoryResponse(
    val id: Long?,
    val name: String,
    val sortOrder: Int,
    val isActive: Boolean,
    val createdAt: String
)
