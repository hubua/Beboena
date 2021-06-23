package com.hubua.beboena.ui

import android.graphics.Typeface
import android.media.MediaPlayer
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import com.hubua.beboena.bl.GeorgianAlphabet
import com.hubua.beboena.bl.toKhucuri
import com.hubua.beboena.utils.KeyboardUtils
import androidx.transition.TransitionManager
import com.hubua.beboena.R
import com.hubua.beboena.bl.AppSettings
import com.hubua.beboena.bl.toReadsAs
import com.hubua.beboena.utils.CircularRevealTransition
import com.hubua.beboena.utils.TextWatcherAdapter

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
