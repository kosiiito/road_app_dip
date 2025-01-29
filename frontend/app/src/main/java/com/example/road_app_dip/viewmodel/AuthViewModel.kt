package com.example.road_app_dip.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.road_app_dip.models.Users
import com.example.road_app_dip.network.ApiService
import com.example.road_app_dip.network.ApiInterface
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject

class AuthViewModel : ViewModel() {
    private val api = ApiService.create<ApiInterface>()

    fun loginUser(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val user = Users(email = email, password = password)

                Log.d("LOGIN_REQUEST", "Email: $email, Password: $password")

                val response = api.loginUser(user)

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d("LOGIN_SUCCESS", "Response: $responseBody")

                    val token = responseBody?.get("token")
                    if (token != null) {
                        onResult(true, "Login successful! Token: $token")
                    } else {
                        onResult(false, "Login failed: No token received")
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Log.e("LOGIN_ERROR", "Code: ${response.code()}, Message: $errorBody")

                    if (errorBody.startsWith("<!DOCTYPE html>")) {
                        onResult(false, "Server returned an HTML page. Check API URL.")
                    } else {
                        val errorJson = try {
                            JSONObject(errorBody).getString("message")
                        } catch (e: Exception) {
                            errorBody
                        }
                        onResult(false, errorJson)
                    }
                }
            } catch (e: Exception) {
                Log.e("LOGIN_EXCEPTION", "Exception: ${e.message}")
                onResult(false, "Exception: ${e.message}")
            }
        }
    }
}
