package com.se114.cafe.network.api

import retrofit2.http.GET

interface ProductApi {

    @GET("api/health")
    suspend fun health(): Map<String, String>

}