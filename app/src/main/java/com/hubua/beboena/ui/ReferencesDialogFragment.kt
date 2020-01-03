package com.hubua.beboena.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.hubua.beboena.R
import kotlinx.android.synthetic.main.dialog_fragment_references.*


class ReferencesDialogFragment : DialogFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val html = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(getString(R.string.txt_references), Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml(getString(R.string.txt_references))
        }

        val txtReferences = view.findViewById<TextView>(R.id.txt_references)
        txtReferences.text = html
        txtReferences.movementMethod = ScrollingMovementMethod()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it, R.style.DialogTheme)

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            val dialogView = requireActivity().layoutInflater.inflate(R.layout.dialog_fragment_references, null)
            onViewCreated(dialogView, savedInstanceState) // Call explicitly

            builder
                .setView(dialogView)
                .setNegativeButton(
                    R.string.btn_cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                        dialog.cancel()
                    })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}