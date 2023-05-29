package com.example.apz.dto.requests

@kotlinx.serialization.Serializable
data class LoginRequest(
    val login: String,
    val password: String
)
