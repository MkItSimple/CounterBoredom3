package com.mkitsimple.counterboredom.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import com.mkitsimple.counterboredom.data.models.User
import com.mkitsimple.counterboredom.data.repositories.UserRepository
import com.mkitsimple.counterboredom.utils.NODE_USERS
import javax.inject.Inject

class FriendsListViewModel @Inject constructor() : ViewModel() {
    @Inject
    lateinit var repository: UserRepository

    private val dbUsers = FirebaseDatabase.getInstance().getReference(NODE_USERS)

    var users: LiveData<List<User>>? = null
    suspend fun fetchUsers() {
        users = repository.fetchUsers()
    }
}