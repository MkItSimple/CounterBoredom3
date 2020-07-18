package com.mkitsimple.counterboredom.ui.auth

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.animation.AnimationUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.mkitsimple.counterboredom.BaseApplication
import com.mkitsimple.counterboredom.R
import com.mkitsimple.counterboredom.ui.main.MainActivity
import com.mkitsimple.counterboredom.utils.Coroutines
import com.mkitsimple.counterboredom.utils.longToast
import com.mkitsimple.counterboredom.viewmodels.ViewModelFactory
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.Job
import javax.inject.Inject

class RegisterActivity : AppCompatActivity() {

    companion object {
        val TAG = "RegisterActivity"
    }

    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var token: String? = null
    private lateinit var viewModel: AuthViewModel

    @Inject
    lateinit var factory: ViewModelFactory
    private lateinit var job1: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        ( this.applicationContext as BaseApplication).appComponent
            .newAuthComponent().inject(this)

        viewModel = ViewModelProviders.of(this, factory)[AuthViewModel::class.java]

        job1 = Job()

        initAnimation()

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    token = task.result!!.token
                }
            }

        textViewAlreadyHaveAccount.setOnClickListener {
            //Log.d(TAG, "Try to show login activity")
            // launch the login activity somehow
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        buttonSelectPhoto.setOnClickListener {
            //Log.d(TAG, "Try to show photo selector")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        buttonRegister.setOnClickListener {
            performRegister()
        }
    }

    private fun initAnimation() {
        val fromtopbottom = AnimationUtils.loadAnimation(this, R.anim.fromtopbottom)
        val fromtopbottomtwo = AnimationUtils.loadAnimation(this, R.anim.fromtopbottomtwo)
        val smalltobig = AnimationUtils.loadAnimation(this, R.anim.smalltobig)

        buttonSelectPhoto.startAnimation(smalltobig)
        editTextUsername.startAnimation(fromtopbottom)
        editTextEmail.startAnimation(fromtopbottom)
        editTextPassword.startAnimation(fromtopbottom)
        buttonRegister.startAnimation(fromtopbottomtwo)
        textViewAlreadyHaveAccount.startAnimation(fromtopbottomtwo)
    }

    var selectedPhotoUri: Uri? = null
    var downloadedPhotoUri: String? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            // proceed and check what the selected image was....
            //Log.d(TAG, "Photo was selected")
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            imageViewSelectPhoto.setImageBitmap(bitmap)
            buttonSelectPhoto.alpha = 0f
        }
    }

    private fun performRegister() {
        val username = editTextUsername.text.toString()
        val email = editTextEmail.text.toString()
        val password = editTextPassword.text.toString()

        if (username.isEmpty()) {
            longToast(getString(R.string.PleaseFillOutUsername))
            return
        }

        if (email.isEmpty()) {
            longToast(getString(R.string.PleaseFillOutEmail))
            return
        }

        if (password.isEmpty()) {
            longToast(getString(R.string.PleaseFillOutPassword))
            return
        }

        Coroutines.main {
            viewModel.performRegister(email, password)
            viewModel.registerResult?.observe(this, Observer {
                if (it == true) {
                    uploadImageToFirebaseStorage()
                } else {
                    longToast("${it}")
                }
            })
        }
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) {
            saveUserToFirebaseDatabase(token)
        } else {
            Coroutines.main {
                viewModel.uploadImageToFirebaseStorage(selectedPhotoUri!!)
                viewModel.uploadResult?.observe(this, Observer {
                    saveUserToFirebaseDatabaseWithProfileImage(it.toString(), token)
                })
            }
        }
    }

    // Save User
    private fun saveUserToFirebaseDatabase(token: String?) {
        Coroutines.main {
            viewModel.saveUserToFirebaseDatabase(editTextUsername.text.toString(), "null", token!!)
            viewModel.saveUserResult?.observe(this, Observer {
                if (it == true) {
                    loginUser()
                } else {
                    longToast("Failed to upload image to storage: $it")
                }
            })
        }
    }

    // Save User with Profile Image
    private fun saveUserToFirebaseDatabaseWithProfileImage(profileImageUrl: String?, token: String?) {
        Coroutines.main {
            viewModel.saveUserToFirebaseDatabase(editTextUsername.text.toString(), profileImageUrl!!, token!!)
            viewModel.saveUserResult?.observe(this, Observer {
                if (it == true) {
                    loginUser()
                } else {
                    longToast("Failed to upload image to storage: ${it}")
                }
            })
        }
    }

    private fun loginUser() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        if(::job1.isInitialized) job1.cancel()
    }
}
