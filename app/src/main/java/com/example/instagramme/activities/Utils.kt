package com.example.instagramme.activities

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.example.instagramme.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_register_email.view.*
import java.time.Duration


class ValueEventListenerAdapter(val handler: (DataSnapshot) -> Unit) : ValueEventListener {
    private val TAG = "ValueEventListenerAdapt"

    override fun onDataChange(data: DataSnapshot) {
        handler(data)
    }

    override fun onCancelled(p0: DatabaseError) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}

@GlideModule
class CustomGlideModule : AppGlideModule() {

}

fun Context.showToast(text: String, duration: Int = Toast.LENGTH_SHORT ){
    Toast.makeText(this, text, duration ).show()
}

fun coordinateBtnAndInputs(btn: Button, vararg inputs: EditText){
    val watcher = object: TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            btn.isEnabled =  inputs.all { it.text.isNotEmpty() }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    }
    inputs.forEach {
        it.addTextChangedListener(watcher)
        btn.isEnabled =  inputs.all { it.text.isNotEmpty() }
    }
}

fun ImageView.loadUserPhoto(photoUrl : String?){
    GlideApp.with(this).load(phoroUrl).fallback(R.drawable.person).into(this)
}