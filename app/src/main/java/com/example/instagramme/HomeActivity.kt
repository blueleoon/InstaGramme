package com.example.instagramme

import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : BaseActivity(0) {
    private val TAG = "BaseActivity"
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        Log.d(TAG, "onCreate")
        setupbottomnavigation()


        auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword("capeselourd@auterter.com", "icicparis")
            .addOnCompleteListener{
                if (it.isSuccessful){
                    Log.d(TAG, "Ca va peser !")
                } else {
                    Log.d(TAG, "Tu postera plus tard ...", it.exception)
                }
            }
    }
}
