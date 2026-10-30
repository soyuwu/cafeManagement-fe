package com.se114.cafe.feature.auth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.*

class LoginViewModel : ViewModel(){
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _uiState.update {
            it.copy(
                email = newEmail,
                message = ""
            )
        }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update {
            it.copy(
                password = newPassword,
                message = ""
            )
        }
    }

    fun login() {
        val currentState = _uiState.value

        val message = when {
            currentState.email.isBlank() ->
                "Email is required"

            !currentState.email.contains("@") ->
                "Email is invalid"

            currentState.password.isBlank() ->
                "Password is required"

            else ->
                "Input looks valid!"
        }

        _uiState.update {
            it.copy(message = message)
        }
    }
}