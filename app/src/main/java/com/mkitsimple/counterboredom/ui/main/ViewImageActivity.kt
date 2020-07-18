package com.mkitsimple.counterboredom.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mkitsimple.counterboredom.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_view_image.*

class ViewImageActivity : AppCompatActivity() {

    companion object {
        const val TAG = "ViewImage"
        const val USER_KEY = "USER_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_image)

        val mImage = intent.getStringExtra("IMAGE_URI")
        val fileName = intent.getStringExtra("IMAGE_FILE_NAME")

        Picasso.get().load(mImage).into(imageView)

        imageView.setOnLongClickListener {
            MyDialogBottomSheet(fileName!!).show(
                supportFragmentManager,
                ""
            )
            return@setOnLongClickListener true
        }

        backArrow.setOnClickListener {
            finish()
        }
    }
}
