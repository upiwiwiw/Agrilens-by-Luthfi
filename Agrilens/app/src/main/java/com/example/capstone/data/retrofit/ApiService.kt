package com.example.capstone.data.retrofit

import com.example.capstone.response.LoginResponse
import com.example.capstone.response.LogoutResponse
import com.example.capstone.response.RegisterResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Interface API Service for managing network calls.
 */
interface ApiService {

    /**
     * Registers a new user.
     *
     * @param name User's full name.
     * @param email User's email address.
     * @param password User's password.
     * @param confirmPassword Confirmation of the user's password.
     * @return A Call object containing [RegisterResponse].
     */
    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("confirmPassword") confirmPassword: String
    ): Call<RegisterResponse>

    /**
     * Logs in an existing user.
     *
     * @param email User's email address.
     * @param password User's password.
     * @return A Call object containing [LoginResponse].
     */
    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    /**
     * Logs out the current user.
     *
     * @return A Call object containing [LogoutResponse].
     */
    @POST("logout")
    fun logout(): Call<LogoutResponse>
}
