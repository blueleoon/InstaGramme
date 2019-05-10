package com.example.instagramme.activities

import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import com.example.instagramme.R
import com.example.instagramme.models.User
import com.example.instagramme.views.PasswordDialog
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_edit_profile.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class EditProfileActivity : AppCompatActivity(), PasswordDialog.Listener {

    private val TAG = "EditProfileActivity"
    private lateinit var mUser: User
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    private lateinit var mStorage: StorageReference
    private lateinit var mPendingUser: User
    val simpleDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss, Locale.FR")
    private lateinit var mImageUri: Uri


    private val TAKE_PICTURE_REQUEST_CODE = 1

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
        change_photo_text.setOnClickListener({ takeCameraPicture() })

        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference
        mStorage = FirebaseStorage.getInstance().reference
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



    private fun takeCameraPicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null){
            val imageFile = createImageFile()
            mImageUri = FileProvider.getUriForFile(
                this,
                "com.example.instagramme.fileprovider",
                imageFile
            )
            intent.putExtra( MediaStore.EXTRA_OUTPUT ,mImageUri)
            startActivityForResult(intent, TAKE_PICTURE_REQUEST_CODE)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name

        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${simpleDateFormat.format(Date())}_",
            ".jpg",
            storageDir
        )
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == TAKE_PICTURE_REQUEST_CODE && resultCode == RESULT_OK){
            val uid = mAuth.currentUser!!.uid
            val ref = mStorage.child("users/$uid/photo")
            ref.putFile(mImageUri).addOnCompleteListener{ it ->
                if(it.isSuccessful){
                    ref.downloadUrl.addOnCompleteListener {
                        val photoUrl = it.result.toString()
                        mDatabase.child("users/$uid/photo").setValue(photoUrl).addOnCompleteListener{
                            if (it.isSuccessful){
                                mUser = mUser.copy(photo = photoUrl)
                                profile_image.loadUserPhoto(mUser.photo)
                            }else{
                                showToast(it.exception!!.message!!)
                            }
                        }
                    }

                }else{
                    showToast(it.exception!!.message!!)
                }
            }
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
            mAuth.currentUser!!.reauthenticate(credential){
                mAuth.currentUser!!.updateEmail(mPendingUser.email){
                    updateUser(mPendingUser)
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

        mDatabase.updateUser(mAuth.currentUser!!.uid, updatesMap) {
            showToast("Profile saved")
            finish()
        }
    }

    private fun validate(user: User): String? =
        when{
            user.name.isEmpty() -> "Please enter name"
            user.username.isEmpty() -> "Please enter username"
            user.website.isEmpty() -> "Please enter website"
            else -> null
        }

    private fun DatabaseReference.updateUser(uid: String, updates: Map<String, Any>, onSuccess: () -> Unit) {
        child("users").child(mAuth.currentUser!!.uid).updateChildren(updates)
            .addOnCompleteListener{
                if (it.isSuccessful){
                    onSuccess()
                } else {
                    showToast("Problem updateUser : "  + it.exception!!.message!!)
                }
            }
    }


    private fun FirebaseUser.updateEmail(email: String, onSuccess: ()-> Unit) {
        updateEmail(email).addOnCompleteListener{
            if (it.isSuccessful){
                onSuccess()
            } else {
                showToast("Problem onPasswordConfirm : "  + it.exception!!.message!!)
            }
        }
    }
    private fun FirebaseUser.reauthenticate(credential: AuthCredential, onSuccess: ()-> Unit) {
        reauthenticate(credential).addOnCompleteListener{
            if (it.isSuccessful){
                onSuccess()
            } else {
                showToast("Problem onPasswordConfirm : "  + it.exception!!.message!!)
            }
        }
    }
}


