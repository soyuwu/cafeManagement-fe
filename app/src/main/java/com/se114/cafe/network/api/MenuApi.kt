package com.se114.cafe.network.api

import com.se114.cafe.network.dto.CategoryResponse
import com.se114.cafe.network.dto.ProductResponse
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.GET

data class MenuSnapshot(val categories: List<CategoryResponse>, val products: List<ProductResponse>)

data class MenuAvailability(val productId: Long, val isAvailable: Boolean)

interface MenuApi {
    @GET("api/menu")
    suspend fun snapshot(@Header("If-None-Match") tag: String?): Response<MenuSnapshot>

    @GET("api/menu/availability")
    suspend fun availability(@Header("If-None-Match") tag: String?): Response<List<MenuAvailability>>

    @GET("api/menu/categories")
    suspend fun getCategories(): List<CategoryResponse>

    @GET("api/menu/products")
    suspend fun getProducts(): List<ProductResponse>
}
