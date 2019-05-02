package com.example.instagramme.activities

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.example.instagramme.R
import com.example.instagramme.models.User
import com.example.instagramme.views.PasswordDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : BaseActivity(0) {
    private val TAG = "EditProfileActivity"
    private lateinit var mUser: User
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        Log.d(TAG, "onCreate")
        close_image.setOnClickListener{
            finish()
        }
        save_image.setOnClickListener{
            updateProfile()
        }

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference
        mDatabase.child("users").child(mAuth.currentUser!!.uid)
            .addListenerForSingleValueEvent(ValueEventListenerAdapter{
            fun onDataChange(data: DataSnapshot) {
                showToast("on create profile ")

                mUser = it.getValue(User::class.java)!!
                name_input.setText(mUser.name, TextView.BufferType.EDITABLE)
                user_input.setText(mUser.username, TextView.BufferType.EDITABLE)
                website_input.setText(mUser.website, TextView.BufferType.EDITABLE)
                bio_input.setText(mUser.bio, TextView.BufferType.EDITABLE)
                email_input.setText(mUser.email, TextView.BufferType.EDITABLE)
                phone_input.setText(mUser.phone.toString(), TextView.BufferType.EDITABLE)
            }
        })
    }

    private fun updateProfile() {
        val user = User(
            name = name_input.text.toString(),
            username = user_input.text.toString(),
            website = website_input.text.toString(),
            bio = bio_input.text.toString(),
            email = email_input.text.toString(),
            phone = phone_input.text.toString()
        )

        val error = validate(user)
        if (error == null){
            if (user.email == mUser.email ) {
                showToast("update profile ")
                updateUser(user)
            } else {
                    PasswordDialog.show(supportFragmentManager, "password_dialog")
            }
        } else {
            showToast(error)
        }
    }

    private fun updateUser(user: User){
        val updatesMap = mutableMapOf<String, Any>()
        if (user.name != mUser.name) updatesMap["name"] = user.name
        if (user.username != mUser.username) updatesMap["username"] = user.username
        if (user.website != mUser.website) updatesMap["website"] = user.website
        if (user.bio != mUser.bio) updatesMap["bio"] = user.bio
        if (user.email != mUser.email) updatesMap["email"] = user.email
        if (user.phone != mUser.phone) updatesMap["phone"] = user.phone

        mDatabase.child("users").child(mAuth.currentUser!!.uid).updateChildren(updatesMap)
            .addOnCompleteListener{
            if (it.isSuccessful){
                showToast("Profile saved")
                finish()
            } else {
                showToast("Problem here : "  + it.exception!!.message!!)
            }
        }
    }

    private fun validate(user: User): String? =
        when{
            user.name.isEmpty() -> "Please enter name"
            user.username.isEmpty() -> "Please enter username"
            user.website.isEmpty() -> "Please enter website"
            else -> null
        }
}


