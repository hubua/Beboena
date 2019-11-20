package com.ghub.beboena.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import com.ghub.beboena.*
import com.ghub.beboena.bl.GeorgianAlphabet

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

