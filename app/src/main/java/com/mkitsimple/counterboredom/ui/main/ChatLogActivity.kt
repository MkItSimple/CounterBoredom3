package com.mkitsimple.counterboredom.ui.main

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.mkitsimple.counterboredom.BaseApplication
import com.mkitsimple.counterboredom.R
import com.mkitsimple.counterboredom.data.models.ChatMessage
import com.mkitsimple.counterboredom.data.models.ImageMessage
import com.mkitsimple.counterboredom.data.models.User
import com.mkitsimple.counterboredom.data.network.Api
import com.mkitsimple.counterboredom.ui.views.ImageFromItem
import com.mkitsimple.counterboredom.ui.views.ImageToItem
import com.mkitsimple.counterboredom.utils.longToast
import com.mkitsimple.counterboredom.utils.toast
import com.mkitsimple.counterboredom.viewmodels.ViewModelFactory
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import kotlinx.android.synthetic.main.custom_layout_toolbar.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import javax.inject.Inject

class ChatLogActivity : AppCompatActivity() {

    companion object {
        const val TAG = "ChatLog"
        const val USER_KEY = "USER_KEY"
    }

    val adapter = GroupAdapter<GroupieViewHolder>()

    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var toUser: User? = null
    var fromId: String? = null
    var toId: String? = null
    var token: String? = null


    private lateinit var viewModel: ChatLogViewModel
    private lateinit var job1: Job
    private lateinit var job2: Job
    private lateinit var job3: Job
    private lateinit var job4: Job

    @Inject
    lateinit var factory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        ( this.applicationContext as BaseApplication).appComponent
            .newMainComponent().inject(this)

        viewModel = ViewModelProviders.of(this, factory)[ChatLogViewModel::class.java]
        job1 = Job()
        job2 = Job()
        job3 = Job()
        job4 = Job()

        recylerViewChatLog.adapter = adapter

        toUser = intent.getParcelableExtra<User>(USER_KEY)
        token = toUser?.token.toString()

        Picasso.get().load(toUser?.profileImageUrl).into(customToolbarChatLogCircleImageView)
        customToolbarChatLogTextView.text = toUser?.username

        fromId = FirebaseAuth.getInstance().uid
        toId = toUser?.uid
        //val uid = mAuth.uid

        backArrow.setOnClickListener {
            finish()
        }
//
        //setDummyData()
        listenForMessages()

        // Attemt to send message
        chatLogSendbutton.setOnClickListener {
            //performSendMessage(token!!)
            performSendMessage()
        }

        // Attempt to send image message
        chatLogSendImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    var selectedPhotoUri: Uri? = null // we put this outide the function . . so that we can use it later on

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data
            uploadImageToFirebaseStorage()
        }
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return

        CoroutineScope(Dispatchers.Main + job1).launch{
            viewModel.uploadImageToFirebaseStorage(selectedPhotoUri!!)
            viewModel.uploadImageResult?.observe(this@ChatLogActivity, Observer {
                if (it.first) {
                    performSendImageMessage(it.second, it.third)
                } else {
                    viewModel.uploadImageErrorMessage.observe(this@ChatLogActivity, Observer { uploadImageErrorMessage ->
                        longToast("Failed to upload image to storage: $uploadImageErrorMessage")
                    })
                }
            })
        }
    }

    private fun listenForMessages() {
        //toast("listenForMessages")
        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        ref.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)
                if (chatMessage != null) {
                    if (chatMessage.type == "TEXT"){
                        if (chatMessage.fromId == mAuth.uid) {
                            adapter.add(ChatFromItem(chatMessage, MainActivity.currentUser, "bukas"))
                        } else {
                            adapter.add(ChatToItem(chatMessage, toUser))
                        }
                    } else {
                        val imageMessage = p0.getValue(ImageMessage::class.java)
                        if (imageMessage!!.fromId == mAuth.uid) {
                            adapter.add(ImageToItem(imageMessage, MainActivity.currentUser!!, supportFragmentManager, "bukas"))
                        } else {
                            adapter.add(ImageFromItem(imageMessage, toUser!!, supportFragmentManager, "bukas"))
                        }
                    }
                    recylerViewChatLog.scrollToPosition(adapter.itemCount - 1)
                }
            }

            override fun onChildRemoved(p0: DataSnapshot) {}

        })
    }

    //    //private fun performSendMessage(token: String) {
    private fun performSendMessage() {
        //toast("performSendImageMessage")
        val text = chatLogEditText.text.toString()

        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMessage = ChatMessage(reference.key!!, text, MainActivity.currentUser!!.uid, toUser!!.uid, System.currentTimeMillis(), "")
        reference.setValue(chatMessage)
            .addOnSuccessListener {
            }
            .addOnFailureListener{
            }

        toReference.setValue(chatMessage)
        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")

        latestMessageRef.setValue(chatMessage)
        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToRef.setValue(chatMessage)

        // send notification
        val retrofit = Retrofit.Builder()
            .baseUrl("https://kotlinmessenger-3bcd8.web.app/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api =
            retrofit.create(
                Api::class.java
            )

        val call = api.sendNotification(token, MainActivity.currentUser!!.username, text)

        call?.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(
                call: Call<ResponseBody?>,
                response: Response<ResponseBody?>
            ) {
                try {
                    toast(response.body()!!.string())
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                call: Call<ResponseBody?>,
                t: Throwable
            ) {
            }
        })


//            CoroutineScope(Dispatchers.Main + job4).launch{
//                viewModel.performSendMessage(toId!!, fromId, text)
//                viewModel.isPerformSendMessageSuccessful?.observe(this@ChatLogActivity, Observer {
//                    if(it){
//                        chatLogEditText.text.clear()
//                        recylerViewChatLog.scrollToPosition(adapter.itemCount - 1)
//                    }
//                })
//
//                // Send notification to receiver
//                viewModel.sendNotification(token!!, MainActivity.currentUser!!.username, text)
//            }
//        }
    }


    private fun performSendImageMessage(fileLocation: String, filename: String) {
        //toast("performSendImageMessage")
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()

        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val imageMessage = ImageMessage(reference.key!!, fileLocation, fromId!!, toId!!, System.currentTimeMillis(), filename)

        reference.setValue(imageMessage)
            .addOnSuccessListener {
            }
            .addOnFailureListener{
            }

        toReference.setValue(imageMessage)

        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageRef.setValue(imageMessage)

        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToRef.setValue(imageMessage)

//        CoroutineScope(Dispatchers.Main + job3).launch{
//            viewModel.performSendImageMessage(toId, fromId, fileLocation, filename)
//            viewModel.isSuccessful.observe(this@ChatLogActivity, Observer { isSuccessful ->
//                if(isSuccessful){
//                    chatLogEditText.text.clear()
//                    recylerViewChatLog.scrollToPosition(adapter.itemCount - 1)
//                }
//            })
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(::job1.isInitialized) job1.cancel()
    }

}

class ChatFromItem(
    val chatMessage: ChatMessage?,
    val user: User?,
    cWhen: String
) : Item<GroupieViewHolder>() {
    override fun getLayout() = R.layout.chat_from_row

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textViewFromRow.text = chatMessage!!.text

        val uri = user!!.profileImageUrl
        val targetImageView = viewHolder.itemView.imageViewFromRow
        if (uri != "null"){
            Picasso.get().load(uri).into(targetImageView)
        }
    }
}


class ChatToItem(
    val chatMessage: ChatMessage?,
    val user: User?
) : Item<GroupieViewHolder>() {
    override fun getLayout() = R.layout.chat_to_row

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textViewToRow.text = chatMessage!!.text

        val uri = user!!.profileImageUrl
        val targetImageView = viewHolder.itemView.imageViewToRow
        if (uri != "null"){
            Picasso.get().load(uri).into(targetImageView)
        }
    }
}
