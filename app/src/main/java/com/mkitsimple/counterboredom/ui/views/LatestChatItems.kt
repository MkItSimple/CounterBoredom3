package com.mkitsimple.counterboredom.ui.views

import android.annotation.SuppressLint
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mkitsimple.counterboredom.R
import com.mkitsimple.counterboredom.data.models.ChatMessage
import com.mkitsimple.counterboredom.data.models.MessageType
import com.mkitsimple.counterboredom.data.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.row_latest_chats.view.*

class LatestChatItems(
    val chatMessage: ChatMessage,
    val mWhen: String
): Item<GroupieViewHolder>() {
    var chatPartnerUser: User? = null

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        var who : String? = "You"
        val chatPartnerId: String
        if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
            chatPartnerId = chatMessage.toId
        } else {
            chatPartnerId = chatMessage.fromId
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(p0: DataSnapshot) {
                chatPartnerUser = p0.getValue(User::class.java)
                viewHolder.itemView.textViewUsernameLatestChats.text = chatPartnerUser?.username

                // Work with timestamp start
                val mWhen = mWhen
                // Work with timestamp end

                // if chat partner has profileImage
                if (chatPartnerUser?.profileImageUrl != "null") {
                    val targetImageView = viewHolder.itemView.circleImageViewLatestChats
                    Picasso.get().load(chatPartnerUser?.profileImageUrl).into(targetImageView)
                }

                // message from who?
                if (chatMessage.fromId != FirebaseAuth.getInstance().uid) {
                    who = chatPartnerUser?.username
                }

                var chatText = chatMessage.text
                if (chatText.length >= 30){
                    chatText  = chatText.substring(0, 30) + ".."
                }

                if (chatMessage.type == MessageType.TEXT) {
                    viewHolder.itemView.textViewMessageLatestMessage.text = "$who: $chatText $mWhen"
                } else {
                    viewHolder.itemView.textViewMessageLatestMessage.text = "$who sent a photo.  $mWhen"
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    override fun getLayout(): Int {
        return R.layout.row_latest_chats
    }
}