package com.se114.cafe.network.api

import com.se114.cafe.network.dto.CategoryRequest
import com.se114.cafe.network.dto.CategoryResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CategoryApi {
    @GET("api/categories")
    suspend fun getCategories(): List<CategoryResponse>

    @POST("api/categories")
    suspend fun createCategory(@Body request: CategoryRequest): CategoryResponse
}
