package com.hubua.beboena.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.hubua.beboena.R

class ContactDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it, R.style.DialogTheme)
                .setTitle(R.string.app_version)
                .setMessage(getString(R.string.txt_contact))
                .setNegativeButton(R.string.btn_close) { dialog, _ -> dialog.cancel() }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}