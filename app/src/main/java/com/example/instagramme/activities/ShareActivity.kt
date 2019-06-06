package com.example.instagramme.activities

import android.os.Bundle
import android.util.Log
import com.example.instagramme.R
import com.example.instagramme.utils.CameraHelper

class ShareActivity : BaseActivity(2) {
    private val TAG = "ShareActivity"
    private lateinit var mCameraHelper: CameraHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setupbottomnavigation()
        Log.d(TAG, "onCreate")

        mCameraHelper = CameraHelper(this)
        mCameraHelper.takeCameraPicture()
    }
}
