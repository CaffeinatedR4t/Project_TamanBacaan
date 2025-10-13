package com.caffeinatedr4t.tamanbacaan.data

data class RegistrationRequest(
    val requestId: String,
    val fullName: String,
    val nik: String,
    val isChild: Boolean,
    val parentName: String?,
    val addressRtRw: String,
    val requestDate: String
)