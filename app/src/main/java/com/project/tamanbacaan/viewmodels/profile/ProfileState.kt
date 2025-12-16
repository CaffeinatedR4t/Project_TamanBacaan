package com.project.tamanbacaan.viewmodels.profile

import com.caffeinatedr4t.tamanbacaan.models.User

sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val user: User) : ProfileState()
    data class Error(val message: String) : ProfileState()
}
