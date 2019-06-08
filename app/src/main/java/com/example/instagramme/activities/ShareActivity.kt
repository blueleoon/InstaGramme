package com.example.instagramme.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.instagramme.R
import com.example.instagramme.models.User
import com.example.instagramme.utils.CameraHelper
import com.example.instagramme.utils.FirebaseHelper
import com.example.instagramme.utils.GlideApp
import com.example.instagramme.utils.ValueEventListenerAdapter
import com.google.firebase.database.ServerValue
import kotlinx.android.synthetic.main.activity_share.*
import org.w3c.dom.Comment
import org.w3c.dom.Text
import java.sql.Date
import java.sql.Timestamp

class ShareActivity : BaseActivity(2) {
    private val TAG = "ShareActivity"
    private lateinit var mCamera: CameraHelper
    private lateinit var mFirebase: FirebaseHelper
    private lateinit var mUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)

        setupbottomnavigation()
        Log.d(TAG, "onCreate")

        mFirebase = FirebaseHelper(this)

        mCamera = CameraHelper(this)
        mCamera.takeCameraPicture()

        back_image.setOnClickListener{ finish() }
        share_text.setOnClickListener{ share() }

        mFirebase.currentUserReference().addValueEventListener(ValueEventListenerAdapter{
            mUser = it.getValue(User::class.java)!!
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == mCamera.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                GlideApp.with(this).load(mCamera.imageUri).centerCrop().into(post_image)
            } else {
                finish()
            }
        }
    }

    private fun share() {
        val imageUri = mCamera.imageUri
        if (mCamera.imageUri != null){
            val uid = mFirebase.auth.currentUser!!.uid
            mFirebase.storage.child("users").child(uid).child("images")
                .child(imageUri!!.lastPathSegment).putFile(imageUri!!).addOnCompleteListener{
                    if (it.isSuccessful){
                        val imageDownloadUrl = imageUri.toString()
                        mFirebase.database.child("images").child(uid).push()
                            .setValue(imageDownloadUrl)
                            .addOnCompleteListener{
                                if (it.isSuccessful){
                                    mFirebase.database.child("feed-posts").child(uid)
                                        .push()
                                        .setValue(mkFeedPost(uid,imageDownloadUrl))
                                        .addOnCompleteListener{
                                            if (it.isSuccessful){
                                                startActivity(Intent(this, ProfileActivity::class.java))
                                                finish()
                                            }
                                        }
                                } else {
                                    showToast(it.exception!!.message!!)
                                }
                            }
                    } else {
                        showToast(it.exception!!.message!!)
                    }
                }
        }
    }

    private fun mkFeedPost(
        uid: String,
        imageDownloadUrl: String
    ): FeedPost {
        return FeedPost(
            uid = uid,
            username = mUser.username,
            image = imageDownloadUrl,
            likescount = 20,
            commentsCount = 20,
            caption = caption_input.text.toString(),
            photo = mUser.photo

        )
    }
}

data class FeedPost(val uid:String = "", val username:String = "", val image:String="", val likescount:Int, val commentsCount:Int,
                    val caption:String="", val comments:List<Comment> = emptyList(), val timestamp: Any = ServerValue.TIMESTAMP, val photo:String?= null) {

    fun timestampDate():Date = Date(timestamp as Long)
}

data class Comment(val uid: String, val username: String, val text: String)