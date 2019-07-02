package com.ghub.beboena

import android.view.ViewGroup
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.view.ViewTreeObserver
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.Window
import com.ghub.beboena.R


open class BaseAppCompatActivity : AppCompatActivity() {

    private var _keyboardListenerAttached = false
    private var _rootLayout: ViewGroup? = null

    private val keyboardLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {

        val heightDiff = _rootLayout!!.rootView.height - _rootLayout!!.height
        val contentViewTop = window.findViewById<View>(Window.ID_ANDROID_CONTENT).top

        val broadcastManager = LocalBroadcastManager.getInstance(this@BaseAppCompatActivity)

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

    protected open fun onShowKeyboard(keyboardHeight: Int) {}
    protected open fun onHideKeyboard() {}

    protected fun attachKeyboardListeners() {

        if (_keyboardListenerAttached) {
            return
        }

        _rootLayout = findViewById<ViewGroup>(R.id.rootLayout)
        _rootLayout!!.viewTreeObserver.addOnGlobalLayoutListener(keyboardLayoutListener)

        _keyboardListenerAttached = true
    }

    override fun onDestroy() {
        super.onDestroy()

        if (_keyboardListenerAttached) {
            _rootLayout!!.viewTreeObserver.removeGlobalOnLayoutListener(keyboardLayoutListener)
        }
    }
}