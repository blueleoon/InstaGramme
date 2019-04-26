package com.example.instagramme.activities

import android.os.Bundle
import android.util.Log
import com.example.instagramme.R

class SearchActivity : BaseActivity(1) {
    private val TAG = "SearchActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setupbottomnavigation()
        Log.d(TAG, "onCreate")
    }
}
