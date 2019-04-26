package com.example.instagramme.activities

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.example.instagramme.R
import com.example.instagramme.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : BaseActivity(0) {
    private val TAG = "EditProfileActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        Log.d(TAG, "onCreate")
        close_image.setOnClickListener{
            finish()
        }

        val auth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance().reference
        database.child("users").child(auth.currentUser!!.uid)
            .addListenerForSingleValueEvent(ValueEventListenerAdapter{
            override fun onDataChange(data: DataSnapshot) {
                val user = it.getValue(User::class.java)
                name_input.setText(user!!.name, TextView.BufferType.EDITABLE)
                user_input.setText(user.username, TextView.BufferType.EDITABLE)
                website_input.setText(user.website, TextView.BufferType.EDITABLE)
                bio_input.setText(user.bio, TextView.BufferType.EDITABLE)
                email_input.setText(user.email, TextView.BufferType.EDITABLE)
                phone_input.setText(user.phone.toString(), TextView.BufferType.EDITABLE)
            }
        })
    }
}

