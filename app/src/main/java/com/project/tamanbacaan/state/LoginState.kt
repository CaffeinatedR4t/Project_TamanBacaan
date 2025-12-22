package com.caffeinatedr4t.tamanbacaan.state

import com.caffeinatedr4t.tamanbacaan.api.model.LoginResponse

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val data: LoginResponse) : LoginState()
    data class Error(val message: String) : LoginState()
}