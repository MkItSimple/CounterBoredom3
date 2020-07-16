package com.mkitsimple.counterboredom.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.mkitsimple.counterboredom.BaseApplication
import com.mkitsimple.counterboredom.R
import com.mkitsimple.counterboredom.ui.main.MainActivity
import com.mkitsimple.counterboredom.viewmodels.ViewModelFactory
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.toast
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: AuthViewModel

    @Inject
    lateinit var factory: ViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        ( this.applicationContext as BaseApplication).appComponent
            .newAuthComponent().inject(this)

        viewModel = ViewModelProviders.of(this, factory)[AuthViewModel::class.java]

        initAnimation()

        buttonLogin.setOnClickListener { performLogin() }
        textViewBackToRegister.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initAnimation() {
        val fromtopbottom = AnimationUtils.loadAnimation(this, R.anim.fromtopbottom)
        val fromtopbottomtwo = AnimationUtils.loadAnimation(this, R.anim.fromtopbottomtwo)
        val smalltobig = AnimationUtils.loadAnimation(this, R.anim.smalltobig)

        imageViewLogo.startAnimation(smalltobig)
        editTextEmail.startAnimation(fromtopbottom)
        editTextPassword.startAnimation(fromtopbottom)
        buttonLogin.startAnimation(fromtopbottomtwo)
        textViewBackToRegister.startAnimation(fromtopbottomtwo)
    }

    private fun performLogin() {
        val email = editTextEmail.text.toString()
        val password = editTextPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            toast(getString(R.string.LoginError))
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to log in: ${it.message}", Toast.LENGTH_SHORT).show()
            }

//        Coroutines.main {
//            viewModel.performLogin(email, password)
//            viewModel.loginResult?.observe(this, Observer {
//                if (it == true) {
//                    startActivity(intentFor<MainActivity>().clearTask().newTask())
//                    //longToast(it.toString())
//                } else {
//                    longToast(it.toString())
//                }
//            })
//        }
    }
}
