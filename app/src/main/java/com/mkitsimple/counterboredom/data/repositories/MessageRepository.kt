package com.mkitsimple.counterboredom.data.repositories

import android.annotation.SuppressLint
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.mkitsimple.counterboredom.data.models.ChatMessage
import com.mkitsimple.counterboredom.data.models.ImageMessage
import com.mkitsimple.counterboredom.data.models.MessageType
import com.mkitsimple.counterboredom.data.network.Api
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.util.*
import kotlin.collections.HashMap

class MessageRepository (val api: Api){
    private var iChatTime: String? = null

    suspend fun performSendMessage(toId: String, fromId: String, text: String): MutableLiveData<Boolean>? {
        val returnValue = MutableLiveData<Boolean>()
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMessage = ChatMessage(reference.key!!, text, fromId, toId, System.currentTimeMillis(), "")
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                returnValue.value = true

            }
            .addOnFailureListener{
                returnValue.value = false
            }

        toReference.setValue(chatMessage)
        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")

        latestMessageRef.setValue(chatMessage)
        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToRef.setValue(chatMessage)

        return returnValue
    }

    suspend fun uploadImageToFirebaseStorage(selectedPhotoUri: Uri): MutableLiveData<Triple<Boolean, String, String>>? {
        val returnValue = MutableLiveData<Triple<Boolean, String, String>>()

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/messages/$filename")

        ref.putFile(selectedPhotoUri)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener {
                        val triple = Triple(true, it.toString(), filename)
                        returnValue.value = triple
                    }
                }
                .addOnFailureListener {
                    val triple = Triple(true, it.message!!, filename)
                    returnValue.value = triple
                }
        return returnValue
    }

    suspend fun listenForMessages(uid: String?): MutableLiveData<Triple<Pair<ChatMessage, ImageMessage>, Pair<String, String>, Int>>? {
        val returnValue = MutableLiveData<Triple<Pair<ChatMessage, ImageMessage>, Pair<String, String>, Int>>()

        val fromId = FirebaseAuth.getInstance().uid
        val toId = uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        ref.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val mChatMessage = p0.getValue(ChatMessage::class.java)
                val mImageMessage = p0.getValue(ImageMessage::class.java)

                //mImageMessage.timestamp
                val mChatMessageWhen = getWhen(mChatMessage!!.timestamp)
                val mImageMessageWhen = getWhen(mImageMessage!!.timestamp)

                // check if 1 text or 2 image
                var mInt = 1
                if (mChatMessage.type == MessageType.IMAGE)
                    mInt = 2

                val triple = Triple(Pair(mChatMessage, mImageMessage), Pair(mChatMessageWhen, mImageMessageWhen), mInt)
                returnValue.value = triple
            }

            override fun onChildRemoved(p0: DataSnapshot) {}

        })

        return returnValue
    }

    suspend fun performSendImageMessage(toId: String?, fromId: String, fileLocation: String, filename: String): MutableLiveData<Boolean>? {
        val returnValue = MutableLiveData<Boolean>()

        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()

        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val imageMessage = ImageMessage(reference.key!!, fileLocation, fromId, toId!!, System.currentTimeMillis(), filename)

        reference.setValue(imageMessage)
                .addOnSuccessListener {
                    returnValue.value = true
                }
                .addOnFailureListener{
                    returnValue.value = false
                }

        toReference.setValue(imageMessage)

        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageRef.setValue(imageMessage)

        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToRef.setValue(imageMessage)

        return returnValue
    }

    suspend fun sendNotification(token: String, username: String, text: String): MutableLiveData<Boolean>? {
        val returnValue = MutableLiveData<Boolean>()

        val call = api.sendNotification(token, username, text)

        call?.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
            ) {
                try {
                    returnValue.value = true
                    //toast(response.body()!!.string())
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

        return returnValue
    }

    suspend fun listenForLatestMessages(): MutableLiveData<Pair<HashMap<String, ChatMessage>, String>>? {
        val returnValue = MutableLiveData<Pair<HashMap<String, ChatMessage>, String>>()

        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")

        val latestMessagesMap = HashMap<String, ChatMessage>()

        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, p1: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
                val mWhen = getWhen(chatMessage.timestamp)

                latestMessagesMap[snapshot.key!!] = chatMessage
                returnValue.value = Pair(latestMessagesMap, mWhen)
            }

            override fun onChildChanged(snapshot: DataSnapshot, p1: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java) ?: return
                val mWhen = getWhen(chatMessage.timestamp)

                latestMessagesMap[snapshot.key!!] = chatMessage
                returnValue.value = Pair(latestMessagesMap, mWhen)
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }
            override fun onChildRemoved(p0: DataSnapshot) {

            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })


        return returnValue

    }

    @SuppressLint("NewApi")
    fun getWhen(chatTimestamp: Long): String{
        val currentTimestamp = System.currentTimeMillis()

        // Chat Timestamp String
        val chatTimestampString = SimpleDateFormat("yyyy MMM ddEEE", Locale.getDefault()).format(chatTimestamp)
        val sChatYear = chatTimestampString.substring(0,11).toUpperCase(Locale.getDefault()) // if more than 1 year
        val sChatMonth = chatTimestampString.substring(4,11).toUpperCase(Locale.getDefault()) // if more than 1 month
        val sChatDay = chatTimestampString.substring(11,14).toUpperCase(Locale.getDefault()) // if more than 1 day
        // Chat Timestamp Number
        val chatTimestampNumber = SimpleDateFormat("yyyyMMdd'T'h:mm a", Locale.getDefault()).format(chatTimestamp)
        val iChatYear = chatTimestampNumber.substring(0,4)
        val iChatMonth = chatTimestampNumber.substring(4,6)
        val iChatDay = chatTimestampNumber.substring(6,8)

        // chat timestamp time
        val timeMPosition = chatTimestampNumber.indexOf("M", 1)
        if ( chatTimestampNumber.substring((timeMPosition - 7),(timeMPosition - 6)) != "T" ){
            iChatTime = chatTimestampNumber.substring((timeMPosition - 7),(timeMPosition+1))
        } else {
            iChatTime = chatTimestampNumber.substring((timeMPosition - 6),(timeMPosition+1))
        }

        // Current Timestamp Number
        val currentTimestampNumber = SimpleDateFormat("yyyyMMdd'T'h:mm a", Locale.getDefault()).format(currentTimestamp)
        val iCurrentYear = currentTimestampNumber.substring(0,4)
        val iCurrentMonth = currentTimestampNumber.substring(4,6)
        val iCurrentDay = currentTimestampNumber.substring(6,8)

        // Calculate number of Years Months Days
        val startLocalDate = LocalDate.of(iChatYear.toInt(), iChatMonth.toInt(), iChatDay.toInt()) // old timestamp
        val endLocalDate = LocalDate.of(iCurrentYear.toInt(), iCurrentMonth.toInt(), iCurrentDay.toInt()) // new timestamp
        //val endLocalDate = LocalDate.of(2020, 7, 2) // new timestamp
        val periodBetween: Period = Period.between(startLocalDate, endLocalDate)

        // Store to variables the count of years, months, and days
        val inputString = periodBetween.toString()
        val yPosition = inputString.indexOf("Y", 0)
        val dPosition = inputString.indexOf("D", 0)
        var yearsCount : String = "0"
        var daysCount : String = "0"

        if (yPosition > -1){
            if (inputString.substring((yPosition - 2), (yPosition - 1)) != "P") {
                yearsCount = inputString.substring((yPosition - 2), (yPosition))
            } else {
                yearsCount = inputString.substring((yPosition - 1), (yPosition))
            }
        }

        if (dPosition > -1){
            val beforeD = inputString.substring((dPosition - 2), (dPosition - 1))
            if (beforeD != "Y" && beforeD != "M" && beforeD != "P") {
                daysCount = inputString.substring((dPosition - 2), (dPosition))
            } else {
                daysCount = inputString.substring((dPosition - 1), (dPosition))
            }
        }

        var mWhen = iChatTime
        if (daysCount.toInt() == 0){ mWhen = iChatTime } // less than 1 day
        if (daysCount.toInt() == 1){ mWhen = "Yesterday AT $iChatTime" } // 1 day
        if (daysCount.toInt() > 1){ mWhen = "$sChatDay AT $iChatTime" } // more than 1 day
        if (daysCount.toInt() > 7){ mWhen = "$sChatMonth AT $iChatTime" } // more than 1 day
        if (yearsCount.toInt() > 0){ mWhen = "$sChatMonth $sChatYear AT $iChatTime" } // more than 1 year

        return mWhen!!
    }
}