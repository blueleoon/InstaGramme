package com.example.instagramme

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.example.instagramme.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_profile.*

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
        val user = auth.currentUser
        val database = FirebaseDatabase.getInstance().reference
        database.child("users").child(user!!.uid).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(data: DataSnapshot) {
                val user = data.getValue(User::class.java)
                name_input.setText(user!!.name, TextView.BufferType.EDITABLE)
                user_input.setText(user.username, TextView.BufferType.EDITABLE)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "onCancelled: ", error.toException() )
            }

        })
    }
}
