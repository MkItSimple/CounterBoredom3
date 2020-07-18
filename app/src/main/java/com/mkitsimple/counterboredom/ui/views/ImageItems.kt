package com.mkitsimple.counterboredom.ui.views

import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.mkitsimple.counterboredom.R
import com.mkitsimple.counterboredom.data.models.ImageMessage
import com.mkitsimple.counterboredom.data.models.User
import com.mkitsimple.counterboredom.ui.main.MyDialogBottomSheet
import com.mkitsimple.counterboredom.ui.main.ViewImageActivity
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.image_from_row.view.*
import kotlinx.android.synthetic.main.image_to_row.view.*

class ImageFromItem(
    val image: ImageMessage,
    val user: User,
    val supportFragmentManager: FragmentManager,
    val mWhen: String
) : Item<GroupieViewHolder>() {

    val filename: String = image.filename

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val mWhen = mWhen
        viewHolder.itemView.fromRowWhen.text = mWhen

        val imageviewFromRow = viewHolder.itemView.imageview_from_row
        Picasso.get().load(image.imagePath).into(imageviewFromRow)

        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageview_chat_from_row
        Picasso.get().load(uri).into(targetImageView)

        imageviewFromRow.setOnClickListener {
            val intent = Intent(it.context, ViewImageActivity::class.java)
            intent.putExtra("IMAGE_URI", image.imagePath)
            it.context.startActivity(intent)
        }

        imageviewFromRow.setOnLongClickListener {
            MyDialogBottomSheet(filename).show(
                supportFragmentManager,
                ""
            )
            return@setOnLongClickListener true
        }
    }

    override fun getLayout(): Int {
        return R.layout.image_from_row
    }
}

class ImageToItem(
    val image: ImageMessage,
    val user: User,
    val supportFragmentManager: FragmentManager,
    val mWhen: String
): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        //val chatTimestamp = image.timestamp
        val mWhen = mWhen
        viewHolder.itemView.toRowWhen.text = mWhen

        val filename: String = image.filename

        val imageviewToRow = viewHolder.itemView.imageview_to_row
        Picasso.get().load(image.imagePath).into(imageviewToRow)

        // load our user image into the star
        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageview_chat_to_row
        Picasso.get().load(uri).into(targetImageView)

        imageviewToRow.setOnClickListener {
            val intent = Intent(it.context, ViewImageActivity::class.java)
            intent.putExtra("IMAGE_URI", image.imagePath)
            intent.putExtra("IMAGE_FILE_NAME", image.filename)
            it.context.startActivity(intent)
        }

        imageviewToRow.setOnLongClickListener {
            MyDialogBottomSheet(filename).show(
                supportFragmentManager,
                ""
            )
            return@setOnLongClickListener true
        }
    }

    private fun showToast(it: View) {
        Toast.makeText(it.context, "Long click", Toast.LENGTH_LONG).show()
    }

    override fun getLayout(): Int {
        return R.layout.image_to_row
    }
}