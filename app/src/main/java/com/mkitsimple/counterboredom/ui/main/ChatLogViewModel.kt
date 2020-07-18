package com.mkitsimple.counterboredom.ui.main

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.mkitsimple.counterboredom.data.models.ChatMessage
import com.mkitsimple.counterboredom.data.models.ImageMessage
import com.mkitsimple.counterboredom.data.models.User
import com.mkitsimple.counterboredom.data.repositories.MessageRepository
import com.mkitsimple.counterboredom.utils.NODE_USERS
import javax.inject.Inject

class ChatLogViewModel @Inject constructor() : ViewModel() {
    @Inject
    lateinit var repository: MessageRepository

    private val dbUsers = FirebaseDatabase.getInstance().getReference(NODE_USERS)

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _chatMessage = MutableLiveData<ChatMessage>()
    val chatMessage: LiveData<ChatMessage>
        get() = _chatMessage

    private val _imageMessage = MutableLiveData<ImageMessage>()
    val imageMessage: LiveData<ImageMessage>
        get() = _imageMessage

    private val _isSuccessful = MutableLiveData<Boolean>()
    val isSuccessful: LiveData<Boolean>
        get() = _isSuccessful


    private val childEventListener = object : ChildEventListener {
        override fun onCancelled(error: DatabaseError) {}
        override fun onChildMoved(snapshot: DataSnapshot, p1: String?) {}
        override fun onChildChanged(snapshot: DataSnapshot, p1: String?) {}
        override fun onChildAdded(snapshot: DataSnapshot, p1: String?) {
            val mChatMessage = snapshot.getValue(ChatMessage::class.java)
            _chatMessage.value = mChatMessage
        }
        override fun onChildRemoved(snapshot: DataSnapshot) {}
    }

    fun getRealtimeUpdates() {
        dbUsers.addChildEventListener(childEventListener)
    }

    var listenForMessagesResultChatMessage: LiveData<Triple<Pair<ChatMessage, ImageMessage>, Pair<String, String>, Int>>? = null
    suspend fun listenForMessages(uid: String?) {
        listenForMessagesResultChatMessage = repository.listenForMessages(uid)
    }

    var isPerformSendMessageSuccessful: LiveData<Boolean>? = null
    suspend fun performSendMessage(toId: String, fromId: String?, text: String) {
        if (fromId == null) return
        isPerformSendMessageSuccessful = repository.performSendMessage(toId, fromId, text)
    }

    var isPerformSendImageMessage: LiveData<Boolean>? = null
    suspend fun performSendImageMessage(
        toId: String?,
        fromId: String?,
        fileLocation: String,
        filename: String
    ) {
        if (fromId == null) return

        isPerformSendImageMessage = repository.performSendImageMessage(toId, fromId, fileLocation, filename)
    }

    // Send Image
    private val _isUploadImageSuccessful = MutableLiveData<Boolean>()
    val isUploadImageSuccessful: LiveData<Boolean>
        get() = _isUploadImageSuccessful

    private val _uploadedImageUri = MutableLiveData<String>()
    val uploadedImageUri: LiveData<String>
        get() = _uploadedImageUri

    private val _uploadImageErrorMessage = MutableLiveData<String>()
    val uploadImageErrorMessage: LiveData<String>
        get() = _uploadImageErrorMessage

    var uploadImageResult: LiveData<Triple<Boolean, String, String>>? = null
    suspend fun uploadImageToFirebaseStorage(selectedPhotoUri: Uri) {
        uploadImageResult = repository.uploadImageToFirebaseStorage(selectedPhotoUri)
    }

    //private lateinit var jobSendNotification: Job
    private val _isNotificationSuccessful = MutableLiveData<Boolean>()
    val isNotificationSuccessful: LiveData<Boolean> get() = _isNotificationSuccessful

    var isSendNotificationSuccessful: LiveData<Boolean>? = null
    suspend fun sendNotification(token: String, username: String, text: String) {
        isSendNotificationSuccessful = repository.sendNotification(token, username, text)
    }
}