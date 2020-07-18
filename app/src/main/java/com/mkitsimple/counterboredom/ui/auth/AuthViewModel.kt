package com.mkitsimple.counterboredom.ui.auth

import android.net.Uri
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

    var registerResult: LiveData<Any>? = null
    suspend fun performRegister(email: String, password: String) {
        registerResult = repository.performRegister(email, password)
    }

    var uploadResult: LiveData<Any>? = null
    suspend fun uploadImageToFirebaseStorage(selectedPhotoUri: Uri) {
        uploadResult = repository.uploadImageToFirebaseStorage(selectedPhotoUri)
    }

    var saveUserResult: LiveData<Any>? = null
    suspend fun saveUserToFirebaseDatabase(username: String, profileImage: String, token: String) {
        saveUserResult = repository.saveUserToFirebaseDatabase(username, profileImage, token)
    }
}