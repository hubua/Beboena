package com.hubua.beboena.ui

import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import com.hubua.beboena.bl.GeorgianAlphabet
import com.hubua.beboena.bl.toKhucuri
import com.hubua.beboena.utils.KeyboardUtils
import kotlinx.android.synthetic.main.fragment_transcript.*
import androidx.transition.TransitionManager
import com.hubua.beboena.R
import com.hubua.beboena.utils.CircularRevealTransition
import com.hubua.beboena.utils.TextWatcherAdapter

val DEBUG_LongestSentenceFirst: Boolean = false
val DEBUG_TwoLettersAreCorrect: Boolean = false

/**
 * A simple [Fragment] subclass.
 */
class TranscriptFragment : Fragment() {

    private val currentLetter = GeorgianAlphabet.Cursor.currentLetter
    private val currentSentences = GeorgianAlphabet.Cursor.currentSentencesShuffled

    private var currentSentenceIndex = 0

    private var transcriptedCorrectCount: Int = 0
    private var transcriptedWrongCount: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transcript, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val txtCurrentLetter: TextView = view.findViewById(R.id.txt_current_letter)
        val spannable = SpannableString(
            String.format(
                resources.getString(R.string.txt_learning_letter),
                currentLetter.letterKeySpelling
            )
        )
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            //ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.colorPrimary)),
            16,
            17,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        txtCurrentLetter.text = spannable

        pb_transcript_progress.max = currentSentences.count()

        if (DEBUG_LongestSentenceFirst) {
            val longestSentence = GeorgianAlphabet.lettersLearnOrdered.flatMap { it.sentences }.maxBy { it.length }
            txt_sentence.text = longestSentence!!.toKhucuri()
            //txt_sentence.text = "აქა აკურთხევდით ა ბ გ დ ე ვ ზ თ ი კ ლ მ ნ ო პ ჟ რ ს ტ უ ფ ქ ღ ყ შ ჩ ც ძ წ ჭ ხ ჯ ჰ".toKhucuri(withCapital = true)
        }

        edt_transcription.addTextChangedListener(object : TextWatcherAdapter {
            override fun afterTextChanged(s: Editable) {
                btn_check.isEnabled = !s.isBlank()
            }
        })

        btn_check.setOnClickListener { onBtnCheckClick() }
        btn_continue.setOnClickListener { onBtnContinueClick(it) } // Is equivalent to 'btn -> onBtnContinueClick(btn)'

        KeyboardUtils.addKeyboardVisibilityListener(
            this.view!!,
            object : KeyboardUtils.OnKeyboardVisibilityListener {
                override fun onVisibilityChange(isVisible: Boolean) {
                    txt_current_letter?.visibility = if (isVisible) View.GONE else View.VISIBLE
                    frame_layout_progress?.visibility = if (isVisible) View.GONE else View.VISIBLE
                }
            })

        showSentenceToTranscript()

        switchControlsState(true)
    }

    private fun onBtnCheckClick() {

        /*if (edt_transcription.text.toString().isBlank()) {
            Toasty.info(context!!, R.string.txt_blank, Toast.LENGTH_SHORT, true).show()
            return
        }*/

        KeyboardUtils.hideKeyboard(this.activity!!)

        var isCorrect = (edt_transcription.text.toString() == currentSentences[currentSentenceIndex])
        if (DEBUG_TwoLettersAreCorrect) {
            isCorrect = (edt_transcription.text.length > 1)
        }

        val txtBannerToShow = if (isCorrect) txt_banner_correct else txt_banner_wrong
        if (isCorrect) transcriptedCorrectCount++ else transcriptedWrongCount++

        val transition = CircularRevealTransition() // Alternatives are Slide(Gravity.LEFT) Fade()
        transition.setDuration(1000)
        transition.addTarget(txtBannerToShow)
        TransitionManager.beginDelayedTransition(txtBannerToShow.parent!! as ViewGroup, transition)

        txtBannerToShow.visibility = View.VISIBLE

        switchControlsState(false)
    }

    private fun onBtnContinueClick(view: View) {

        if (currentSentenceIndex + 1 < currentSentences.count()) {
            currentSentenceIndex++
        } else {
            // Navigation is done to action instead of fragment (R.id.frg_result) to allow back-stack directly to the home
            val action = TranscriptFragmentDirections.actionFrgTranscriptToFrgResult(
                transcriptedCorrectCount,
                transcriptedWrongCount
            )
            view.findNavController().navigate(action)
        }

        showSentenceToTranscript()

        switchControlsState(true)
    }

    private fun showSentenceToTranscript() {
        pb_transcript_progress.progress = currentSentenceIndex + 1
        txt_transcript_progress.text = "${currentSentenceIndex + 1} / ${currentSentences.count()}"
        txt_sentence.text = currentSentences[currentSentenceIndex].toKhucuri(withCapital = true)
    }

    private fun switchControlsState(newSentence: Boolean) {
        // New sentence or Check sentence

        if (newSentence) {
            edt_transcription.isFocusableInTouchMode = true
            edt_transcription.isFocusable = true
            edt_transcription.text.clear()
            edt_transcription.requestFocus()

        } else {
            edt_transcription.isFocusableInTouchMode = false
            edt_transcription.isFocusable = false
        }

        if (newSentence) {
            txt_banner_correct.visibility = View.GONE
            txt_banner_wrong.visibility = View.GONE
            btn_check.isEnabled = false
        }

        btn_check.visibility = if (newSentence) View.VISIBLE else View.GONE
        btn_continue.visibility = if (newSentence) View.GONE else View.VISIBLE
    }

}
