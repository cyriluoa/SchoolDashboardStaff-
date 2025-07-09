package com.example.schooldashboardstaff.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schooldashboardstaff.data.model.User
import com.example.schooldashboardstaff.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel()
 {

    private val _loginResult = MutableLiveData<Result<User>>()
    val loginResult: LiveData<Result<User>> = _loginResult

    fun login(username: String, password: String){
        _loginResult.value = Result.failure((Exception("Logging in...")))
        authRepository.loginWithUsername(
            username = username,
            password = password,
            onSuccess = { user ->
                _loginResult.value = Result.success(user)
            },
            onFailure = { exception ->
                _loginResult.value = Result.failure(exception)

            }
        )
    }

    fun isLoggedIn(): Boolean {
        return authRepository.isUserLoggedIn()
    }

    fun signOut() {
        authRepository.signOut()
    }
}