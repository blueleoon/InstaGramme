package com.example.instagramme

import android.os.Bundle
import android.util.Log

class HomeActivity : BaseActivity(0) {
    private val TAG = "BaseActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        Log.d(TAG, "onCreate")
        setupbottomnavigation()
    }
}
