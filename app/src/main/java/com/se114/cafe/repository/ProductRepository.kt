package com.se114.cafe.repository

import com.se114.cafe.network.api.ProductApi
import com.se114.cafe.network.RetrofitClient
import com.se114.cafe.network.dto.ProductAvailabilityRequest
import com.se114.cafe.network.dto.ProductRequest

class ProductRepository(private val api: ProductApi = RetrofitClient.retrofit.create(ProductApi::class.java)) {
    suspend fun getProducts() = api.getProducts()

    suspend fun createProduct(request: ProductRequest) = api.createProduct(request)

    suspend fun updateProduct(id: Long, request: ProductRequest) = api.updateProduct(id, request)

    suspend fun updateAvailability(id: Long, isAvailable: Boolean) =
        api.updateAvailability(id, ProductAvailabilityRequest(isAvailable))
}
