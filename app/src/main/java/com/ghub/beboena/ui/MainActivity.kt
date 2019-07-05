package com.ghub.beboena.ui

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.ghub.beboena.*
import com.ghub.beboena.bl.GeorgianAlphabet

class MainActivity : AppCompatActivity(), LettersHomeFragment.OnFragmentInteractionListener {

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val strOga = applicationContext.assets.open("oga.tsv")
        val strSentences = applicationContext.assets.open("sentences.txt")
        GeorgianAlphabet.initialize(strOga, strSentences);

        val letters = GeorgianAlphabet.lettersToLearn

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
