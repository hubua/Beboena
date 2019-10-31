package com.ghub.beboena.ui

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.ghub.beboena.bl.GeorgianAlphabet
import com.ghub.beboena.bl.toChar
import com.ghub.beboena.bl.toKhucuri
import com.ghub.beboena.utils.KeyboardUtils
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_transcript.*
import androidx.transition.TransitionManager
import com.ghub.beboena.R
import com.ghub.beboena.utils.CircularRevealTransition

/**
 * A simple [Fragment] subclass.
 */
class TranscriptFragment : Fragment() {

    private val currentLetter = GeorgianAlphabet.Cursor.currentLetter

    private var currentSentenceIndex = 0

    private var transcriptedCorrectCount: Int = 0
    private var transcriptedWrongCount: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transcript, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val txtCurrentLetter: TextView = view.findViewById(R.id.txt_current_letter)
        val spannable = SpannableString(
            String.format(
                resources.getString(R.string.txt_learning_letter),
                currentLetter.letterId
            )
        )
        spannable.setSpan(
            ForegroundColorSpan(Color.MAGENTA),
            14,
            15,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        txtCurrentLetter.text = spannable

        pb_transcript_progress.max = currentLetter.sentences.count()

        pb_transcript_progress.progress = currentSentenceIndex + 1
        txt_transcript_progress.text =
            "${currentSentenceIndex + 1} / ${currentLetter.sentences.count()}"
        txt_sentence.text = currentLetter.sentences[currentSentenceIndex].toKhucuri()

        //TODO REMOVE DEBUG longestSentence
        val longestSentence =
            GeorgianAlphabet.lettersByOrderIndex.flatMap { it.sentences }.maxBy { it.length }
        txt_sentence.text = longestSentence!!.toKhucuri()
        ////

        edt_transcription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                btn_check.isEnabled = !s.isBlank()
            }
        })
        edt_transcription.text.clear()
        edt_transcription.isFocusableInTouchMode = true
        edt_transcription.isFocusable = true

        txt_transcripted_correct.visibility = View.GONE
        txt_transcripted_wrong.visibility = View.GONE

        /*txt_wrong.animate().alpha(0.0f)
        txt_wrong.animate().translationX(0.0f)*/

        btn_check.isEnabled = false
        btn_check.visibility = View.VISIBLE
        btn_check.setOnClickListener { onBtnCheckClick(it) } //TODO sort out the mess with it, this, view, btn

        btn_continue.visibility = View.GONE
        btn_continue.setOnClickListener { btn -> onBtnContinueClick(btn) } //TODO sort out the mess with it, this, view, btn

        KeyboardUtils.addKeyboardVisibilityListener(
            this.view!!,
            object : KeyboardUtils.OnKeyboardVisibilityListener {
                override fun onVisibilityChange(isVisible: Boolean) {
                    txt_current_letter?.visibility = if (isVisible) View.GONE else View.VISIBLE
                    frame_layout_progress?.visibility = if (isVisible) View.GONE else View.VISIBLE
                }
            })
    }

    private fun onBtnCheckClick(view: View) {

        if (edt_transcription.text.toString().isBlank()) {
            Toasty.info(context!!, R.string.txt_blank, Toast.LENGTH_SHORT, true).show()
            return
        }

        KeyboardUtils.hideKeyboard(this.activity!!)

        //TODO REMOVE DEBUG correct if more than one letter
        //val isCorrect = (edt_transcription.text.toString() == currentLetter.sentences[currentSentenceIndex])
        val isCorrect = (edt_transcription.text.length > 1)

        val txtTranscripted = if (isCorrect) txt_transcripted_correct else txt_transcripted_wrong
        if (isCorrect) transcriptedCorrectCount++ else transcriptedWrongCount++

        edt_transcription.isFocusableInTouchMode = false
        edt_transcription.isFocusable = false

        val transition = CircularRevealTransition() // Slide(Gravity.LEFT) // Fade()
        transition.setDuration(1000)
        transition.addTarget(txtTranscripted)
        TransitionManager.beginDelayedTransition(txtTranscripted.parent!! as ViewGroup, transition)

        txtTranscripted.visibility = View.VISIBLE

        /*txt_wrong.animate().alpha(1.0f).setDuration(1000);
        txt_wrong.animate().translationX(100.0f).setDuration(1000)*/

        btn_check.visibility = View.GONE
        btn_continue.visibility = View.VISIBLE

    }

    private fun onBtnContinueClick(view: View) {

        if (currentSentenceIndex + 1 < currentLetter.sentences.count()) {
            currentSentenceIndex++
        } else {
            // Navigation is done to action instead of fragment (R.id.frg_result) to allow back-stack directly to the home
            view.findNavController()
                .navigate(TranscriptFragmentDirections.actionFrgTranscriptToFrgResult(transcriptedCorrectCount, transcriptedWrongCount))
        }

        pb_transcript_progress.progress = currentSentenceIndex + 1
        txt_transcript_progress.text =
            "${currentSentenceIndex + 1} / ${currentLetter.sentences.count()} c$transcriptedCorrectCount w$transcriptedWrongCount"
        txt_sentence.text = currentLetter.sentences[currentSentenceIndex].toKhucuri()

        edt_transcription.text.clear()
        edt_transcription.isFocusableInTouchMode = true
        edt_transcription.isFocusable = true

        txt_transcripted_correct.visibility = View.GONE
        txt_transcripted_wrong.visibility = View.GONE

        /*txt_wrong.animate().alpha(0.0f)
        txt_wrong.animate().translationX(0.0f)*/

        btn_check.isEnabled = false
        btn_check.visibility = View.VISIBLE

        btn_continue.visibility = View.GONE

    }

}
