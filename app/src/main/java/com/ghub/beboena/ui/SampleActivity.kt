package com.ghub.beboena

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.ghub.beboena.ui.BaseAppCompatActivity
import kotlinx.android.synthetic.main.activity_sample.*

const val EXTRA_MESSAGE = "com.myfirstapp.MESSAGE"

class SampleActivity : BaseAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)

        attachKeyboardListeners()
    }

    /** Called when the user taps the Send button */
    fun sendMessage(view: View) {
        val editText = findViewById<EditText>(R.id.et_message)
        val message = editText.text.toString()
        val intent = Intent(this, LearnLetterActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE, message)
        }
        startActivity(intent)

    }

    override fun onHideKeyboard() {
        tvHello.text = "hidden"
    }


}
