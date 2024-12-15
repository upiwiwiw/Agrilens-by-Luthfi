package com.example.capstone.response

import com.google.gson.annotations.SerializedName

data class RegisterResponse(
    @field:SerializedName("error") val error: Boolean? = null,
    @field:SerializedName("message") val message: String? = null
)

data class LoginResponse(
    @field:SerializedName("loginResult") val loginResult: LoginResult? = null,
    @field:SerializedName("error") val error: Boolean? = null,
    @field:SerializedName("message") val message: String? = null
)

data class LoginResult(
    @field:SerializedName("name") val name: String? = null,
    @field:SerializedName("userId") val userId: String? = null,
)

data class LogoutResponse(
    @field:SerializedName("success") val success: Boolean? = null,
    @field:SerializedName("message") val message: String? = null
)