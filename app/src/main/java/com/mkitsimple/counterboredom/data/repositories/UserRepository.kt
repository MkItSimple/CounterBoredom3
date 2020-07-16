package com.mkitsimple.counterboredom.data.repositories

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.mkitsimple.counterboredom.data.models.ChatMessage
import com.mkitsimple.counterboredom.data.models.User
import java.util.*
import kotlin.collections.HashMap

class UserRepository {

    val uid = FirebaseAuth.getInstance().uid // fromid
    val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$uid")

    val latestMessagesMap = HashMap<String, ChatMessage>()

    suspend fun fetchCurrentUser() : MutableLiveData<User> {
        val returnValue = MutableLiveData<User>()
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                returnValue.value = p0.getValue(User::class.java)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

        return returnValue
    }

    fun uid() : String? {
        return uid
    }

    suspend fun updateProfile(
        username: String,
        curretUser: User?
    ): MutableLiveData<Any> {
        val returnValue = MutableLiveData<Any>()
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(uid, username, curretUser!!.profileImageUrl, curretUser.token)
        ref.setValue(user)
            .addOnSuccessListener {
                returnValue.value = true
            }
            .addOnFailureListener {
                returnValue.value = it.message
            }
        return returnValue
    }

    suspend fun updateProfileWithImage(
        username: String,
        profileImageUrl: String,
        token: String
    ): MutableLiveData<Any> {
        val returnValue = MutableLiveData<Any>()
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(uid, username, profileImageUrl, token)
        ref.setValue(user)
            .addOnSuccessListener {
                returnValue.value = true
            }
            .addOnFailureListener {
                returnValue.value = it.message
            }
        return returnValue
    }

    suspend fun fetchUsers(): MutableLiveData<List<User>>? {
        val users = MutableLiveData<List<User>>()

        val ref = FirebaseDatabase.getInstance().getReference("/users")
            .orderByChild("username")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                val musers = mutableListOf<User>()

                snapshot.children.forEach {
                    val user = it.getValue(User::class.java)
                    if (user != null) {
                        musers.add(user)
                    }
                }
                users.value = musers

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
        return users
    }

    suspend fun uploadImageToFirebaseStorage(selectedPhotoUri: Uri): MutableLiveData<Pair<Boolean, String>>? {
        val returnValue = MutableLiveData<Pair<Boolean, String>>()

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri)
                .addOnSuccessListener {
                    //Log.d(RegisterActivity.TAG, "Successfully uploaded image: ${it.metadata?.path}")
                    ref.downloadUrl.addOnSuccessListener {
                        val pair = Pair(true, it.toString())
                        returnValue.value = pair
                    }
                }
                .addOnFailureListener {
                    val pair = Pair(true, it.message!!)
                    returnValue.value = pair
                }

        return returnValue
    }
}