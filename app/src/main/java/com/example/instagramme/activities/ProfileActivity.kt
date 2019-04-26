package com.example.instagramme.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.instagramme.R
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : BaseActivity(4) {
    private val TAG = "ProfileActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

       setupbottomnavigation()
        Log.d(TAG, "onCreate")

        edit_profile_btn.setOnClickListener{
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }
    }
}
