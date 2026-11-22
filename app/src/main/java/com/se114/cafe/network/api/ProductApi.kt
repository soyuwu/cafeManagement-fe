package com.se114.cafe.network.api

import com.se114.cafe.network.dto.ProductAvailabilityRequest
import com.se114.cafe.network.dto.ProductRequest
import com.se114.cafe.network.dto.ProductResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProductApi {

    @GET("api/products")
    suspend fun getProducts(): List<ProductResponse>

    @POST("api/products")
    suspend fun createProduct(@Body request: ProductRequest): ProductResponse

    @PUT("api/products/{id}")
    suspend fun updateProduct(@Path("id") id: Long, @Body request: ProductRequest): ProductResponse

    @PATCH("api/products/{id}/availability")
    suspend fun updateAvailability(@Path("id") id: Long, @Body request: ProductAvailabilityRequest): ProductResponse
}
