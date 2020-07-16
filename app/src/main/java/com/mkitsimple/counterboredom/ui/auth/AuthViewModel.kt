package com.mkitsimple.counterboredom.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.mkitsimple.counterboredom.data.repositories.AuthRepository
import javax.inject.Inject

class AuthViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var repository: AuthRepository

    var loginResult: LiveData<Any>? = null
    suspend fun performLogin(email: String, password: String) {
        loginResult = repository.performLogin(email, password)
    }
}