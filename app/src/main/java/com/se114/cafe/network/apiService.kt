package com.se114.cafe.network

import retrofit2.http.GET

interface ApiService {

    @GET("api/health")
    suspend fun health(): Map<String, String>

}