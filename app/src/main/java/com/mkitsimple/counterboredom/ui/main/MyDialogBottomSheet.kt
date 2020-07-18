package com.mkitsimple.counterboredom.ui.main

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mkitsimple.counterboredom.R
import kotlinx.android.synthetic.main.my_dialog_bottom_sheet.*

class MyDialogBottomSheet(val filename: String) : BottomSheetDialogFragment() {

    private var storageReference: StorageReference? = null
    var ref: StorageReference? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.my_dialog_bottom_sheet, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        tvSaveImage.setOnClickListener {
            if (filename != ""){
                download(it, filename)
            }
        }

        tvCancelSave.setOnClickListener {
            dismiss()
        }
    }

    private fun download(it: View, filename: String) {
        storageReference = FirebaseStorage.getInstance().reference
        ref = storageReference!!.child("messages").child(filename)
        ref!!.downloadUrl.addOnSuccessListener { uri ->
            dismiss()
            Toast.makeText(context, getString(R.string.ImageSaved), Toast.LENGTH_LONG).show()
            val url = uri.toString()
            downloadFiles(
                it.context,
                filename,
                Environment.DIRECTORY_DOWNLOADS,
                url
            )
        }.addOnFailureListener {}
    }

    private fun downloadFiles(
        context: Context,
        fileName: String,
        destinationDirectory: String,
        url: String
    ) {
        val downloadManager =
            context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(url)
        val request = DownloadManager.Request(uri)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalFilesDir(
            context,
            destinationDirectory,
            "$fileName.jpeg"
        )
        downloadManager.enqueue(request)
    }
}