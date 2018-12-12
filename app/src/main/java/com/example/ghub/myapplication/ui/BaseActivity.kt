/*package com.example.ghub.myapplication.ui

import android.view.ViewGroup
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.view.Window.ID_ANDROID_CONTENT
import android.view.ViewTreeObserver
import android.app.Activity
import com.example.ghub.myapplication.R


class BaseActivity : Activity() {
    private val keyboardLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        val heightDiff = rootLayout!!.rootView.height - rootLayout!!.height
        val contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop()

        val broadcastManager = LocalBroadcastManager.getInstance(this@BaseActivity)

        if (heightDiff <= contentViewTop) {
            onHideKeyboard()

            val intent = Intent("KeyboardWillHide")
            broadcastManager.sendBroadcast(intent)
        } else {
            val keyboardHeight = heightDiff - contentViewTop
            onShowKeyboard(keyboardHeight)

            val intent = Intent("KeyboardWillShow")
            intent.putExtra("KeyboardHeight", keyboardHeight)
            broadcastManager.sendBroadcast(intent)
        }
    }

    private var keyboardListenersAttached = false
    private var rootLayout: ViewGroup? = null

    protected fun onShowKeyboard(keyboardHeight: Int) {}
    protected fun onHideKeyboard() {}

    protected fun attachKeyboardListeners() {
        if (keyboardListenersAttached) {
            return
        }

        rootLayout = findViewById(R.id.rootLayout) as ViewGroup
        rootLayout!!.viewTreeObserver.addOnGlobalLayoutListener(keyboardLayoutListener)

        keyboardListenersAttached = true
    }

    override fun onDestroy() {
        super.onDestroy()

        if (keyboardListenersAttached) {
            rootLayout!!.viewTreeObserver.removeGlobalOnLayoutListener(keyboardLayoutListener)
        }
    }
}*/