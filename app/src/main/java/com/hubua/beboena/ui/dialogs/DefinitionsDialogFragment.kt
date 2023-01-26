package com.hubua.beboena.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.hubua.beboena.R

class DefinitionsDialogFragment : DialogFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val html = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(getString(R.string.txt_definitions), Html.FROM_HTML_MODE_COMPACT)
        } else {
            @Suppress("DEPRECATION")
            Html.fromHtml(getString(R.string.txt_definitions))
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
            val dialogView = requireActivity().layoutInflater.inflate(R.layout.dialog_fragment_definitions, null)
            onViewCreated(dialogView, savedInstanceState) // Call explicitly

            builder
                .setView(dialogView)
                .setNegativeButton(R.string.btn_close) { dialog, _ -> dialog.cancel() }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}