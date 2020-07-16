package com.mkitsimple.counterboredom.utils

import android.content.Context
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

fun Context.toast(message: String){
    Toast.makeText(this, message, Toast.LENGTH_SHORT ).show()
}

fun Context.longToast(message: String){
    Toast.makeText(this, message, Toast.LENGTH_LONG ).show()
}

fun View.longSnackbar(message: String){
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).also { snackbar ->
        snackbar.setAction("Ok") {
            snackbar.dismiss()
        }
    }.show()
}

//fun View.snackbarForever(message: String){
//    Snackbar.make(this, message, Snackbar.LENGTH_INDEFINITE).also { snackbar ->
//        snackbar.setAction("Ok") {
//            snackbar.dismiss()
//        }
//    }.show()
//}