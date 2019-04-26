package com.example.instagramme

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : BaseActivity(0) {
    private val TAG = "HomeActivity"
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        Log.d(TAG, "onCreate")
        setupbottomnavigation()


        mAuth = FirebaseAuth.getInstance()
        sign_out_text.setOnClickListener{
            mAuth.signOut()
        }
        mAuth.addAuthStateListener {
            if (it.currentUser == null){
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
        //mAuth.signOut()
        /*mAuth.signInWithEmailAndPassword("capeselourd@auterter.com", "icicparis")
            .addOnCompleteListener{
                if (it.isSuccessful){
                    Log.d(TAG, "Ca va peser !")
                } else {
                    Log.d(TAG, "Tu postera plus tard ...", it.exception)
                }
            }*/
    }

    override fun onStart() {
        super.onStart()
        if (mAuth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
