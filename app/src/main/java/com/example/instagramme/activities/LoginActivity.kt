package com.example.instagramme.activities

import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.instagramme.R
import kotlinx.android.synthetic.main.activity_login.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

class LoginActivity : AppCompatActivity(), KeyboardVisibilityEventListener, View.OnClickListener {
    private val TAG = "LoginActivity"
    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.d(TAG, "onCreate")

        KeyboardVisibilityEvent.setEventListener(this,this)
        coordinateBtnAndInputs(login_btn, email_input, password_input_register)
        login_btn.setOnClickListener(this)
        create_account_text.setOnClickListener(this)

        mAuth = FirebaseAuth.getInstance()
    }

    override fun onClick(view: View) {
        when (view.id){
            R.id.login_btn -> {
                val email = email_input.text.toString()
                val password = password_input_register.text.toString()
                if (validate(email,password)){
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener{
                        if (it.isSuccessful) {
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        }
                    }
                } else {
                    showToast("Please enter email / password")
                }
            }
            R.id.create_account_text -> {
                startActivity(Intent(this, RegisterActivity::class.java))
            }
        }
    }

    override fun onVisibilityChanged(isKeyboardOpen: Boolean) {
        if (isKeyboardOpen){
            create_account_text.visibility = View.GONE
        }else {
            create_account_text.visibility = View.VISIBLE
        }
    }



    private fun validate (email: String, password: String) =
        email.isNotEmpty() && password.isNotEmpty()

}
