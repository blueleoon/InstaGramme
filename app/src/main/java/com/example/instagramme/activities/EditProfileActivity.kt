package com.example.instagramme.activities

import android.os.Bundle
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.instagramme.R
import com.example.instagramme.models.User
import com.example.instagramme.utils.CameraHelper
import com.example.instagramme.utils.FirebaseHelper
import com.example.instagramme.utils.ValueEventListenerAdapter
import com.example.instagramme.views.PasswordDialog
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.database.DataSnapshot
import kotlinx.android.synthetic.main.activity_edit_profile.*


class EditProfileActivity : AppCompatActivity(), PasswordDialog.Listener {

    private val TAG = "EditProfileActivity"
    private lateinit var mUser: User
    private lateinit var mPendingUser: User
    private lateinit var mFirebaseHelper: FirebaseHelper
    private lateinit var cameraHelper: CameraHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        Log.d(TAG, "onCreate")
        cameraHelper = CameraHelper(this)
        close_image.setOnClickListener{
            finish()
        }
        save_image.setOnClickListener{
            updateProfile()
        }
        change_photo_text.setOnClickListener({ cameraHelper.takeCameraPicture() })

        mFirebaseHelper = FirebaseHelper(this)

        mFirebaseHelper.currentUserReference()
            .addListenerForSingleValueEvent(ValueEventListenerAdapter {
                fun onDataChange(data: DataSnapshot) {
                    mUser = it.getValue(User::class.java)!!
                    name_input.setText(mUser.name)
                    user_input.setText(mUser.username)
                    website_input.setText(mUser.website)
                    bio_input.setText(mUser.bio)
                    email_input.setText(mUser.email)
                    phone_input.setText(mUser.phone?.toString())
                    profile_image.loadUserPhoto(mUser.photo)
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == cameraHelper.REQUEST_CODE && resultCode == RESULT_OK){
            mFirebaseHelper.downloadUserPhoto(mUser, profile_image)
        }
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
        return User(
            name = name_input.text.toString(),
            username = user_input.text.toString(),
            email = email_input.text.toString(),
            website = website_input.text.toStringOrNull(),
            bio =bio_input.text.toStringOrNull(),
            phone = phone_input.text.toString().toLongOrNull()!!
        )
    }


    override fun onPasswordConfirm(password: String) {
        if (password.isNotEmpty()){
            Log.d(TAG, "onPasswordConfirm password : $password")
            PasswordDialog().show(supportFragmentManager, "password_dialog")
            val credential = EmailAuthProvider.getCredential(mUser.email, password)
            mFirebaseHelper.reauthenticate(credential){
                mFirebaseHelper.updateEmail(mPendingUser.email){
                    updateUser(mPendingUser)
                }
            }

        } else {
            showToast("Enter password   ")
        }
    }

    private fun updateUser(user: User){
        val updatesMap = mutableMapOf<String, Any?>()
        if (user.name != mUser.name) updatesMap["name"] = user.name
        if (user.username != mUser.username) updatesMap["username"] = user.username
        if (user.website != mUser.website) updatesMap["website"] = user.website
        if (user.bio != mUser.bio) updatesMap["bio"] = user.bio
        if (user.email != mUser.email) updatesMap["email"] = user.email
        if (user.phone != mUser.phone) updatesMap["phone"] = user.phone

        mFirebaseHelper.updateUser(updatesMap) {
            showToast("Profile saved")
            finish()
        }
    }

    private fun validate(user: User): String? =
        when{
            user.name.isEmpty() -> "Please enter name"
            user.username.isEmpty() -> "Please enter username"
            user.website!!.isEmpty() -> "Please enter website"
            else -> null
        }
}


