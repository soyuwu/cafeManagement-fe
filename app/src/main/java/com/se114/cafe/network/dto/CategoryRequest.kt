package com.se114.cafe.network.dto

data class CategoryRequest(
    val name: String,
    val sortOrder: Int = 0,
    val isActive: Boolean = true
)
