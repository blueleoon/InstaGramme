package com.example.instagramme.utils

import android.app.Activity
import android.net.Uri
import com.example.instagramme.activities.loadUserPhoto
import com.example.instagramme.activities.showToast
import com.example.instagramme.models.User
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import de.hdodenhof.circleimageview.CircleImageView

@Suppress("SpellCheckingInspection")
class FirebaseHelper(private val activity: Activity){
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val mDatabase: DatabaseReference = FirebaseDatabase.getInstance()
        .reference
    private val mStorage: StorageReference = FirebaseStorage.getInstance()
        .reference

    fun uploadUserPhoto(photo: Uri,
                        onSuccess: (UploadTask.TaskSnapshot) -> Unit){
        mStorage.putFile(photo).addOnCompleteListener{ it ->
            if(it.isSuccessful){
                onSuccess(it.result!!)
            }else{
                activity.showToast(it.exception!!.message!!)
            }
        }
    }

    fun updateUserPhoto(photoUrl: String,
                                                  onSuccess: () -> Unit) {
        mDatabase.child("users/${mAuth.currentUser!!.uid}/photo").setValue(photoUrl).addOnCompleteListener{
            if (it.isSuccessful){
                onSuccess()
            }else{
                activity.showToast(it.exception!!.message!!)
            }
        }
    }

    fun updateUser(updates: Map<String, Any?>, onSuccess: () -> Unit) {
        mDatabase.child("users").child(mAuth.currentUser!!.uid).updateChildren(updates)
            .addOnCompleteListener{
                if (it.isSuccessful){
                    onSuccess()
                } else {
                    activity.showToast("Problem updateUser : "  + it.exception!!.message!!)
                }
            }
    }

    fun updateEmail(email: String, onSuccess: ()-> Unit) {
        mAuth.currentUser!!.updateEmail(email).addOnCompleteListener{
            if (it.isSuccessful){
                onSuccess()
            } else {
                activity.showToast("Problem onPasswordConfirm : "  + it.exception!!.message!!)
            }
        }
    }

    fun reauthenticate(credential: AuthCredential, onSuccess: ()-> Unit) {
        mAuth.currentUser!!.reauthenticate(credential).addOnCompleteListener{
            if (it.isSuccessful){
                onSuccess()
            } else {
                activity.showToast("Problem onPasswordConfirm : "  + it.exception!!.message!!)
            }
        }
    }

    fun downloadUserPhoto(user: User, profile_image: CircleImageView?){
        val ref = mStorage.child("users/${mAuth.currentUser!!.uid}/photo")
        ref.downloadUrl.addOnCompleteListener {
            val photoUrl = it.result.toString()
            updateUserPhoto(photoUrl) {
                if (it.isSuccessful){
                    val mUser = user.copy(photo = photoUrl)
                    profile_image!!.loadUserPhoto(mUser.photo)
                }else{
                    activity.showToast(it.exception!!.message!!)
                }
            }
        }
    }

    fun currentUserReference(): DatabaseReference =
        mDatabase.child("users").child(mAuth.currentUser!!.uid)
}