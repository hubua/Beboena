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
import com.hubua.beboena.databinding.FragmentResultBinding
import com.hubua.beboena.databinding.FragmentTranscriptBinding
import com.hubua.beboena.utils.CircularRevealTransition
import com.hubua.beboena.utils.TextWatcherAdapter

/**
 * A simple [Fragment] subclass.
 */
class TranscriptFragment : Fragment() {

    private val currentLetter = GeorgianAlphabet.Cursor.currentLetter
    private val currentSentences = GeorgianAlphabet.Cursor.currentSentences

    private var currentSentenceIndex = 0

    private var transcriptedCorrectCount: Int = 0
    private var transcriptedWrongCount: Int = 0

    private var mediaPlayerCorrect: MediaPlayer? = null
    private var mediaPlayerWrong: MediaPlayer? = null

    private var _binding: FragmentTranscriptBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView.
    private val bindingExists get() = _binding != null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTranscriptBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        mediaPlayerCorrect?.release()
        mediaPlayerCorrect = null
        mediaPlayerWrong?.release()
        mediaPlayerWrong = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val spannable = SpannableString(String.format(resources.getString(R.string.txt_learning_letter), currentLetter.mkhedruli))
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            //ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.colorPrimary)),
            spannable.length - 1,
            spannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.txtCurrentLetter.text = spannable

        binding.pbTranscriptProgress.max = currentSentences.count()

        binding.edtTranscription.hint = if (currentLetter.letterReadsAsSpells) resources.getString(R.string.txt_translate_sentence_hint) else resources.getString(R.string.txt_translate_sentence_hint_as_reads)

        /*
        // DEBUG Longest Sentence First
        val longestSentence = GeorgianAlphabet.lettersLearnOrdered.flatMap { it.sentences }.maxBy { it.length }
        txt_sentence.text = longestSentence!!.toKhucuri()
        txt_sentence.text = "აქა აკურთხევდით ა ბ გ დ ე ვ ზ თ ი კ ლ მ ნ ო პ ჟ რ ს ტ უ ფ ქ ღ ყ შ ჩ ც ძ წ ჭ ხ ჯ ჰ".toKhucuri(withCapital = true)
         */

        binding.edtTranscription.addTextChangedListener(object : TextWatcherAdapter {
            override fun afterTextChanged(s: Editable) {
                binding.btnCheck.isEnabled = !s.isBlank()
            }
        })

        binding.btnCheck.setOnClickListener { onBtnCheckClick() }
        binding.btnContinue.setOnClickListener { onBtnContinueClick(it) } // Is equivalent to 'btn -> onBtnContinueClick(btn)'

        KeyboardUtils.addKeyboardVisibilityListener(
            this.requireView(),
            object : KeyboardUtils.OnKeyboardVisibilityListener {
                override fun onVisibilityChange(isVisible: Boolean) {
                    if (!bindingExists) // binding is only valid between onCreateView and onDestroyView.
                        return
                    binding.txtCurrentLetter.visibility = if (isVisible) View.GONE else View.VISIBLE
                    binding.frameLayoutProgress.visibility = if (isVisible) View.GONE else View.VISIBLE
                }
            })

        showSentenceToTranscript()

        switchControlsState(true)

        mediaPlayerCorrect = MediaPlayer.create(context, R.raw.answer_correct)
        mediaPlayerWrong = MediaPlayer.create(context, R.raw.answer_wrong)

        /*
        // DEBUG Show transcription prompt
        txt_sentence.setOnTouchListener { v, event ->
            txt_sentence.text =
                if (event?.actionMasked == MotionEvent.ACTION_DOWN) {
                    currentSentences[currentSentenceIndex].toReadsAs()
                }
                else {
                    currentSentences[currentSentenceIndex].toKhucuri(isAllCaps = AppSettings.isAllCaps)
                }
            true
        }
        */
    }

    private fun onBtnCheckClick() {

        KeyboardUtils.hideKeyboard(this.requireActivity())

        val isCorrect = (binding.edtTranscription.text.toString() == currentSentences[currentSentenceIndex].toReadsAs())

        /*
        // DEBUG Two Letters Are Correct
        isCorrect = (binding.edtTranscription.text.length > 1)
         */

        val txtBannerToShow = if (isCorrect) binding.txtBannerCorrect else binding.txtBannerWrong
        if (isCorrect) transcriptedCorrectCount++ else transcriptedWrongCount++

        val transition = CircularRevealTransition() // Alternatives are Slide(Gravity.LEFT) Fade()
        transition.setDuration(1000)
        transition.addTarget(txtBannerToShow)
        TransitionManager.beginDelayedTransition(txtBannerToShow.parent!! as ViewGroup, transition)

        txtBannerToShow.visibility = View.VISIBLE

        switchControlsState(false)

        if (AppSettings.isEnableSounds) {
            if (isCorrect) {
                mediaPlayerCorrect?.start()
            } else {
                mediaPlayerWrong?.start()
            }
        }
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
        binding.pbTranscriptProgress.progress = currentSentenceIndex + 1
        binding.txtTranscriptProgress.text = "${currentSentenceIndex + 1} / ${currentSentences.count()}"
        binding.txtSentence.text = currentSentences[currentSentenceIndex].toKhucuri(isAllCaps = AppSettings.isAllCaps)
    }

    private fun switchControlsState(newSentence: Boolean) {
        // New sentence or Check sentence

        if (newSentence) {
            binding.edtTranscription.isFocusableInTouchMode = true
            binding.edtTranscription.isFocusable = true
            binding.edtTranscription.text.clear()
            binding.edtTranscription.requestFocus()

        } else {
            binding.edtTranscription.isFocusableInTouchMode = false
            binding.edtTranscription.isFocusable = false
        }

        if (newSentence) {
            binding.txtBannerCorrect.visibility = View.GONE
            binding.txtBannerWrong.visibility = View.GONE
            binding.btnCheck.isEnabled = false
        }

        binding.btnCheck.visibility = if (newSentence) View.VISIBLE else View.GONE
        binding.btnContinue.visibility = if (newSentence) View.GONE else View.VISIBLE
    }

}
