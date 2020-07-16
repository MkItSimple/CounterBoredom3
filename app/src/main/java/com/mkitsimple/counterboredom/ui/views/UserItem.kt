package com.mkitsimple.counterboredom.ui.views

import com.mkitsimple.counterboredom.R
import com.mkitsimple.counterboredom.data.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.row_friends_list.view.*

class UserItem(val user: User) : Item<GroupieViewHolder>() {
    override fun getLayout() = R.layout.row_friends_list

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.ftv.text = user.username
        val targetImageView = viewHolder.itemView.friendsListCircleImageView
        val uri = user.profileImageUrl
        if (uri != "null") {
            Picasso.get().load(user.profileImageUrl).into(targetImageView)
        }
    }
}