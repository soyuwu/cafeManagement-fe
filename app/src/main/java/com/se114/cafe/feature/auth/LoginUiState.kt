package com.se114.cafe.feature.auth

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val message: String = "",
    val isLoading: Boolean = false
)