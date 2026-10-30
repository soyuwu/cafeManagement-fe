package com.se114.cafe.network.api

import com.se114.cafe.network.dto.AuthRequest
import com.se114.cafe.network.dto.AuthResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun login(
        @Body request: AuthRequest
    ): AuthResponse
}