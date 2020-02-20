package com.hubua.beboena.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.DialogFragment
import com.hubua.beboena.R


class TipsDialogFragment : DialogFragment() {

    var webviewScrollable: WebView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val layoutRoot = view.findViewById<LinearLayout>(R.id.layout_root)

        val webviewTips = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            LollipopFixedWebView(context)
        } else {
            WebView(context)
        }
        webviewTips.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        layoutRoot.addView(webviewTips) //TODO show scrollbar

        webviewTips.webViewClient = WebViewClient()

        webviewTips.loadUrl("file:///android_asset/html/tips.html");
        webviewScrollable = webviewTips
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it, R.style.DialogTheme)

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            val dialogView = requireActivity().layoutInflater.inflate(R.layout.dialog_fragment_tips, null)
            onViewCreated(dialogView, savedInstanceState) // Call explicitly

            builder
                .setView(dialogView)
                .setPositiveButton(
                    R.string.btn_scroll,
                    DialogInterface.OnClickListener { _, _ ->
                        // Do nothing here because we override this button later to change the close behaviour.
                    })
                .setNegativeButton(
                    R.string.btn_close,
                    DialogInterface.OnClickListener { dialog, _ ->
                        dialog.cancel()
                    })

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }


    override fun onResume() {
        super.onResume()

        val scrollButton: Button = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
        scrollButton.setOnClickListener {
            webviewScrollable!!.pageDown(false)
        }
    }
}

class LollipopFixedWebView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : WebView(context?.createConfigurationContext(Configuration()), attrs, defStyleAttr, defStyleRes)