package com.ghub.beboena

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText

class MainActivity : AppCompatActivity(), LearnLetterFragment.OnFragmentInteractionListener, TranscriptSentencesFragment.OnFragmentInteractionListener {

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()


    }

    fun btnClick(view: View) {

        val intent = Intent(this, LearnLetterActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE, "msg1")
        }
        startActivity(intent)

    }
}
