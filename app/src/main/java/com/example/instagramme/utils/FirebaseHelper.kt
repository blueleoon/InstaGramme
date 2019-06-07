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
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val database: DatabaseReference = FirebaseDatabase.getInstance()
        .reference
    val storage: StorageReference = FirebaseStorage.getInstance()
        .reference

    fun uploadUserPhoto(photo: Uri,
                        onSuccess: (UploadTask.TaskSnapshot) -> Unit){
        storage.putFile(photo).addOnCompleteListener{ it ->
            if(it.isSuccessful){
                onSuccess(it.result!!)
            }else{
                activity.showToast(it.exception!!.message!!)
            }
        }
    }

    fun updateUserPhoto(photoUrl: String,
                                                  onSuccess: () -> Unit) {
        database.child("users/${auth.currentUser!!.uid}/photo").setValue(photoUrl).addOnCompleteListener{
            if (it.isSuccessful){
                onSuccess()
            }else{
                activity.showToast(it.exception!!.message!!)
            }
        }
    }

    fun updateUser(updates: Map<String, Any?>, onSuccess: () -> Unit) {
        database.child("users").child(auth.currentUser!!.uid).updateChildren(updates)
            .addOnCompleteListener{
                if (it.isSuccessful){
                    onSuccess()
                } else {
                    activity.showToast("Problem updateUser : "  + it.exception!!.message!!)
                }
            }
    }

    fun updateEmail(email: String, onSuccess: ()-> Unit) {
        auth.currentUser!!.updateEmail(email).addOnCompleteListener{
            if (it.isSuccessful){
                onSuccess()
            } else {
                activity.showToast("Problem onPasswordConfirm : "  + it.exception!!.message!!)
            }
        }
    }

    fun reauthenticate(credential: AuthCredential, onSuccess: ()-> Unit) {
        auth.currentUser!!.reauthenticate(credential).addOnCompleteListener{
            if (it.isSuccessful){
                onSuccess()
            } else {
                activity.showToast("Problem onPasswordConfirm : "  + it.exception!!.message!!)
            }
        }
    }

    fun downloadUserPhoto(user: User, profile_image: CircleImageView?){
        val ref = storage.child("users/${auth.currentUser!!.uid}/photo")
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
        database.child("users").child(auth.currentUser!!.uid)
}