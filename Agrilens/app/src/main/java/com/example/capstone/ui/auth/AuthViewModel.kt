package com.example.capstone.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.capstone.data.repository.AuthRepository
import com.example.capstone.response.LoginResponse
import com.example.capstone.response.RegisterResponse

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _registerResult = MutableLiveData<RegisterResponse>()
    val registerResult: LiveData<RegisterResponse> = _registerResult

    private val _loginResult = MutableLiveData<LoginResponse>()
    val loginResult: LiveData<LoginResponse> = _loginResult

    private val _error = MutableLiveData<String>() // Tambahkan LiveData untuk error handling
    val error: LiveData<String> = _error

    fun register(name: String, email: String, password: String, confirmPassword: String) {
        authRepository.registerUser(name, email, password, confirmPassword) { result ->
            if (result.error == true) {
                // Jika terjadi error, update LiveData error
                _error.postValue(result.message ?: "Registration failed")
            } else {
                // Jika berhasil, update LiveData registerResult
                _registerResult.postValue(result)
            }
        }
    }

    fun login(email: String, password: String) {
        authRepository.loginUser(email, password) { result ->
            if (result.error == true) {
                // Jika terjadi error, update LiveData error
                _error.postValue(result.message ?: "Login failed")
            } else {
                // Jika berhasil, update LiveData loginResult
                _loginResult.postValue(result)
            }
        }
    }
}
