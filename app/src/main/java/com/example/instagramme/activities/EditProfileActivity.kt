package com.example.instagramme.activities

import android.os.Bundle
import android.arch.lifecycle.Observer
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import com.example.instagramme.R
import com.example.instagramme.models.User
import com.example.instagramme.views.PasswordDialog
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : AppCompatActivity(), PasswordDialog.Listener {

    private val TAG = "EditProfileActivity"
    private lateinit var mUser: User
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    private lateinit var mPendingUser: User
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
            mPendingUser = readInputs()

        val error = validate(mPendingUser)
        if (error == null){
            if (mPendingUser.email == mUser.email ) {
                showToast("update profile ")
                updateUser(mPendingUser)
            } else {

            }
        } else {
            showToast(error)
        }
    }

    private fun readInputs(): User {
        val phoneStr = phone_input.text.toString()
        return User(
            name = name_input.text.toString(),
            username = user_input.text.toString(),
            website = website_input.text.toString(),
            bio = bio_input.text.toString(),
            email = email_input.text.toString(),
            phone = if (phoneStr.isEmpty()) 0.toString() else phoneStr
        )
    }

    override fun onPasswordConfirm(password: String) {
        if (password.isNotEmpty()){
            Log.d(TAG, "onPasswordConfirm password : $password")
            PasswordDialog().show(supportFragmentManager, "password_dialog")
            val credential = EmailAuthProvider.getCredential(mUser.email, password)
            mAuth.currentUser!!.reauthenticate(credential).addOnCompleteListener{
                if (it.isSuccessful){
                    mAuth.currentUser!!.updateEmail(mPendingUser.email).addOnCompleteListener{
                        if (it.isSuccessful){
                            updateUser(mPendingUser)
                        } else {
                            showToast("Problem onPasswordConfirm : "  + it.exception!!.message!!)
                        }
                    }
                } else {
                    showToast("Problem onPasswordConfirm : "  + it.exception!!.message!!)
                }
            }

        } else {
            showToast("Enter password   ")
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
                showToast("Problem updateUser : "  + it.exception!!.message!!)
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


