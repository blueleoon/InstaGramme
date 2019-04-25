package com.example.instagramme

import android.os.Bundle
import android.util.Log

class ProfileActivity : BaseActivity(4) {
    private val TAG = "ProfileActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

       setupbottomnavigation()
        Log.d(TAG, "onCreate")
    }
}
