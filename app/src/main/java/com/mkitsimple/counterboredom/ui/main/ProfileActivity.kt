package com.mkitsimple.counterboredom.ui.main

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.mkitsimple.counterboredom.BaseApplication
import com.mkitsimple.counterboredom.R
import com.mkitsimple.counterboredom.utils.longToast
import com.mkitsimple.counterboredom.viewmodels.ViewModelFactory
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("DEPRECATION")
class ProfileActivity : AppCompatActivity() {

    private lateinit var viewModel: ProfileViewModel
    private lateinit var job1: Job
    private lateinit var job2: Job
    private lateinit var job3: Job

    @Inject
    lateinit var factory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        ( this.applicationContext as BaseApplication).appComponent
            .newMainComponent().inject(this)

        viewModel = ViewModelProviders.of(this, factory).get(ProfileViewModel::class.java)

        if (MainActivity.currentUser?.profileImageUrl != "null") {
            Picasso.get().load(MainActivity.currentUser?.profileImageUrl)
                .into(circleImageViewProfile)
        }

        editTextProfile.setText(MainActivity.currentUser!!.username)

        circleImageViewProfile.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        buttonSaveChanges.setOnClickListener {
            uploadImageToFirebaseStorage()
        }

        profileBackArrow.setOnClickListener {
            finish()
        }
        initJobs()
    }

    private fun initJobs() {
        job1 = Job()
        job2 = Job()
        job3 = Job()
    }

    var selectedPhotoUri: Uri? = null
    var bitmap: Bitmap? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            // proceed and check what the selected image was...
            selectedPhotoUri = data.data
            bitmap = MediaStore.Images.Media
                .getBitmap(this.contentResolver, selectedPhotoUri)

            circleImageViewProfile.setImageBitmap(bitmap)
        }
    }

    private fun uploadImageToFirebaseStorage() {
        val username = editTextProfile.text.toString()

        if(username.isEmpty()){
            longToast(getString(R.string.please_fill_out_username))
            return
        }

        if(username.length < 2){
            longToast(getString(R.string.username_must_be_more_than_2_characters))
            return
        }

        if (username.length > 20) {
            longToast(getString(R.string.username_should_not_be_more_than_20_characters))
            return
        }

        if (username == MainActivity.currentUser!!.username && selectedPhotoUri == null) {
            longToast(getString(R.string.there_is_no_changes_has_been_made))
            return
        }

        CoroutineScope(Dispatchers.Main + job1).launch{
            if (selectedPhotoUri == null) {
                updateProfile()
            } else {
                viewModel.uploadImageToFirebaseStorage(selectedPhotoUri!!)
                viewModel.isUploadSuccessful?.observe(this@ProfileActivity, androidx.lifecycle.Observer {
                    if (it.first) {
                        updateProfileWithImage(it.second)
                    }
                })
            }
        }
    }

    private fun updateProfile() {
        CoroutineScope(Dispatchers.Main + job2).launch{
            viewModel.updateProfile(editTextProfile.text.toString(), MainActivity.currentUser)
            viewModel.isSuccessful?.observe(this@ProfileActivity, androidx.lifecycle.Observer {
                if (it == true) {
                    Toast.makeText(applicationContext, getString(R.string.Profile_successfully_updated), Toast.LENGTH_LONG).show()
                    updateCircleImageViewProfile()

                }
            })
        }
    }

    private fun updateProfileWithImage(profileImageUrl: String) {
        CoroutineScope(Dispatchers.Main + job3).launch{
            viewModel.updateProfileWithImage(editTextProfile.text.toString(), profileImageUrl, MainActivity.currentUser!!.token)
            viewModel.isSuccessful2?.observe(this@ProfileActivity, androidx.lifecycle.Observer {
                if(it == true){
                    Toast.makeText(applicationContext, getString(R.string.Profile_successfully_updated), Toast.LENGTH_LONG).show()
                    updateCircleImageViewProfile()
                }
            })
        }
    }

    private fun updateCircleImageViewProfile(){
        if (selectedPhotoUri != null){
            circleImageViewProfile.setImageBitmap(bitmap)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(::job1.isInitialized) job1.cancel()
        if(::job2.isInitialized) job2.cancel()
        if(::job3.isInitialized) job3.cancel()
    }
}
