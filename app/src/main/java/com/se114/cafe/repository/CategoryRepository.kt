package com.se114.cafe.repository

import com.se114.cafe.network.RetrofitClient
import com.se114.cafe.network.api.CategoryApi
import com.se114.cafe.network.dto.CategoryRequest

class CategoryRepository(private val api: CategoryApi = RetrofitClient.retrofit.create(CategoryApi::class.java)) {
    suspend fun getCategories() = api.getCategories()

    suspend fun createCategory(request: CategoryRequest) = api.createCategory(request)
}
