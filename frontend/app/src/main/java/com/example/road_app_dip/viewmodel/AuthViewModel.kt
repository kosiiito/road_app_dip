package com.example.road_app_dip.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.road_app_dip.models.Users
import com.example.road_app_dip.network.ApiService
import com.example.road_app_dip.network.ApiInterface
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val api = ApiService.create<ApiInterface>()

    fun loginUser(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val user = Users(email = email, password = password)
                val response = api.loginUser(user)

                if (response.isSuccessful && response.body() != null) {
                    Log.d("LOGIN_SUCCESS", "Response: ${response.body()}")
                    onResult(true, null)
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Invalid email or password"
                    Log.e("LOGIN_ERROR", "Code: ${response.code()}, Message: $errorMessage")
                    onResult(false, errorMessage)
                }
            } catch (e: Exception) {
                Log.e("LOGIN_EXCEPTION", "Exception: ${e.message}")
                onResult(false, e.message)
            }
        }
    }

    fun registerUser(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val user = Users(email = email, password = password)
                val response = api.registerUser(user)

                if (response.isSuccessful && response.body() != null) {
                    Log.d("REGISTER_SUCCESS", "User registered: ${response.body()}")
                    onResult(true, null)
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Registration failed"
                    Log.e("REGISTER_ERROR", "Code: ${response.code()}, Message: $errorMessage")
                    onResult(false, errorMessage)
                }
            } catch (e: Exception) {
                Log.e("REGISTER_EXCEPTION", "Exception: ${e.message}")
                onResult(false, e.message)
            }
        }
    }
}
