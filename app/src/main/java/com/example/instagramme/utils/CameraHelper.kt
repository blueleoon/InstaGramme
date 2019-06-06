package com.example.instagramme.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CameraHelper(private val activity: Activity) {
    var imageUri: Uri? = null
    val REQUEST_CODE = 1
    private val simpleDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss, Locale.FR")

    fun takeCameraPicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(activity.packageManager) != null){
            val imageFile = createImageFile()
            imageUri = FileProvider.getUriForFile(
                activity,
                "com.example.instagramme.fileprovider",
                imageFile
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri)
            activity.startActivityForResult(intent, REQUEST_CODE)
        }
    }

    @Throws(IOException::class)
    fun createImageFile(): File {
        // Create an image file name

        val storageDir: File = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${simpleDateFormat.format(Date())}_",
            ".jpg",
            storageDir
        )
    }
}