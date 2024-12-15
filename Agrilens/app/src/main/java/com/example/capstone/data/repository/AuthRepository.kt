package com.example.capstone.data.repository

import com.example.capstone.data.retrofit.ApiService
import com.example.capstone.response.LoginResponse
import com.example.capstone.response.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository(
    private val registerService: ApiService,
    private val loginService: ApiService
) {

    // Fungsi helper untuk mengurangi duplikasi pada enqueue
    private fun <T> makeRequest(call: Call<T>, onSuccess: (T) -> Unit, onError: (String) -> Unit) {
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    response.body()?.let { onSuccess(it) } ?: onError("Unknown response")
                } else {
                    onError("Error: ${response.message()} (Code: ${response.code()})")
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                onError("Network Error: ${t.localizedMessage}")
            }
        })
    }

    fun registerUser(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        callback: (RegisterResponse) -> Unit
    ) {
        val call = registerService.register(name, email, password, confirmPassword)
        makeRequest(call,
            onSuccess = { callback(it) },
            onError = { callback(RegisterResponse(error = true, message = it)) }
        )
    }

    fun loginUser(email: String, password: String, callback: (LoginResponse) -> Unit) {
        val call = loginService.login(email, password)
        makeRequest(call,
            onSuccess = { callback(it) },
            onError = { callback(LoginResponse(error = true, message = it)) }
        )
    }
}
