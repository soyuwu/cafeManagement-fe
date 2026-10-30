package com.se114.cafe.network.dto

data class AuthResponse(
    val token: String,
    val userId: Long,
    val fullName: String,
    val role: String
)