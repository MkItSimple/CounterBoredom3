package com.mkitsimple.counterboredom.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.mkitsimple.counterboredom.data.models.ChatMessage
import com.mkitsimple.counterboredom.data.repositories.MessageRepository
import javax.inject.Inject

class LatestChatsViewModel @Inject constructor() : ViewModel() {
    @Inject
    lateinit var repository: MessageRepository

    var listenForLatestMessagesResult: LiveData<Pair<HashMap<String, ChatMessage>, String>>? = null
    suspend fun listenForLatestMessages() {
        listenForLatestMessagesResult = repository.listenForLatestMessages()
    }
}