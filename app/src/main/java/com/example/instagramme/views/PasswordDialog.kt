package com.example.instagramme.views

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import com.example.instagramme.R

class PasswordDialog: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.dialog_password, null)
        return AlertDialog.Builder(context!!)
            .setView(view)
            .setPositiveButton(android.R.string.ok, { _ , _ ->

            })
            .setNegativeButton(android.R.string.cancel, {_,_ ->

            })
            .setTitle(R.string.please_enter_password)
            .create()
    }
}