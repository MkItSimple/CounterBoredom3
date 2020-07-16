package com.mkitsimple.counterboredom.data.repositories

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.mkitsimple.counterboredom.data.models.User
import java.util.*

class AuthRepository {

    val mAuth = FirebaseAuth.getInstance()

    suspend fun performLogin(email: String, password: String) : MutableLiveData<Any> {
        val loginReturnValue = MutableLiveData<Any>()

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    loginReturnValue.value = true
                }
                .addOnFailureListener {
                    loginReturnValue.value = it.message
                }

        return loginReturnValue
    }

    suspend fun performRegister(email: String, password: String): MutableLiveData<Any> {
        val registerReturnValue = MutableLiveData<Any>()
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                registerReturnValue.value = true
            }
            .addOnFailureListener{
                registerReturnValue.value = it.message
            }
        return registerReturnValue
    }

    suspend fun uploadImageToFirebaseStorage(selectedPhotoUri: Uri): MutableLiveData<Any> {
        val uploadReturnValue = MutableLiveData<Any>()
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri)
                .addOnSuccessListener {

                    ref.downloadUrl.addOnSuccessListener {
                        uploadReturnValue.value = it
                    }
                }
                .addOnFailureListener {
                    uploadReturnValue.value = it.message
                }
        return  uploadReturnValue
    }

    suspend fun saveUserToFirebaseDatabase(username: String, profileImage: String, token: String): MutableLiveData<Any> {
        val uploadReturnValue = MutableLiveData<Any>()

        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, username, profileImage, token)

        ref.setValue(user)
                .addOnSuccessListener {
                    uploadReturnValue.value = true
                }
                .addOnFailureListener {
                    uploadReturnValue.value = it.message
                }
        return uploadReturnValue
    }
}