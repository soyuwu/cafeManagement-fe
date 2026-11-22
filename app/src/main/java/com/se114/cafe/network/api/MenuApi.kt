package com.se114.cafe.network.api

import com.se114.cafe.network.dto.CategoryResponse
import com.se114.cafe.network.dto.ProductResponse
import retrofit2.http.GET

interface MenuApi {
    @GET("api/menu/categories")
    suspend fun getCategories(): List<CategoryResponse>

    @GET("api/menu/products")
    suspend fun getProducts(): List<ProductResponse>
}
