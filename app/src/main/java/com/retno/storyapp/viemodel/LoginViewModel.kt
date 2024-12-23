package com.retno.storyapp.viemodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.retno.storyapp.api.ApiConfig
import com.retno.storyapp.models.LoginRequest
import com.retno.storyapp.models.LoginResponse
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val loginRequest = LoginRequest(email, password)
                val response = ApiConfig.getApiService().login(loginRequest)

                if (response.isSuccessful && response.body() != null && !response.body()!!.error!!) {
                    _loginResult.value = Result.success(response.body()!!)
                } else {
                    _loginResult.value = Result.failure(Exception("Login failed"))
                }
            } catch (e: Exception) {
                _loginResult.value = Result.failure(e)
            }
        }
    }
}
