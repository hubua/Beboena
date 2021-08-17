package com.hubua.beboena.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hubua.beboena.R
import com.hubua.beboena.bl.GeorgianAlphabet

/**
 * A simple [Fragment] subclass.
 */
class ResemblesFragment : Fragment() {

    private val currentLetter = GeorgianAlphabet.Cursor.currentLetter

    private var mediaPlayerCorrect: MediaPlayer? = null
    private var mediaPlayerWrong: MediaPlayer? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_resembles, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //txt_current_letter.text = currentLetter.mkhedruli.toString()

        mediaPlayerCorrect = MediaPlayer.create(context, R.raw.answer_correct)
        mediaPlayerWrong = MediaPlayer.create(context, R.raw.answer_wrong)

    }

    override fun onDestroyView() {
        mediaPlayerCorrect?.release()
        mediaPlayerCorrect = null
        mediaPlayerWrong?.release()
        mediaPlayerWrong = null
        super.onDestroyView()
    }


}
