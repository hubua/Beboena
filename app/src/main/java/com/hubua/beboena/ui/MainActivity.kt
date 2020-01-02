package com.hubua.beboena.ui

import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hubua.beboena.*
import com.hubua.beboena.bl.GeorgianAlphabet

class MainActivity : AppCompatActivity(), LettersHomeFragment.OnFragmentInteractionListener {

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // GeorgianAlphabet initialization must happen first
        val strOga = applicationContext.assets.open("oga.tsv")
        val strSentences = applicationContext.assets.open("sentences.txt")
        GeorgianAlphabet.initialize(strOga, strSentences);

        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        GeorgianAlphabet.Cursor.initialize(sharedPref)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()

    }


}

