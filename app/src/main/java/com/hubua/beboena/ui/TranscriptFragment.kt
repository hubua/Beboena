package com.hubua.beboena.ui

import android.graphics.Typeface
import android.media.MediaPlayer
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.transition.TransitionManager
import com.hubua.beboena.R
import com.hubua.beboena.bl.*
import com.hubua.beboena.databinding.FragmentTranscriptBinding
import com.hubua.beboena.utils.CircularRevealTransition
import com.hubua.beboena.utils.KeyboardUtils
import com.hubua.beboena.utils.TextWatcherAdapter

/**
 * A simple [Fragment] subclass.
 */
class TranscriptFragment : Fragment() {

    private val cursor = GeorgianAlphabet.Cursor

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

        val spannable = SpannableString(String.format(resources.getString(R.string.txt_learning_letter), cursor.currentLetter.mkhedruli))
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            //ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.colorPrimary)),
            spannable.length - 1,
            spannable.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.txtCurrentLetter.text = spannable

        binding.pbTranscriptProgress.max = cursor.currentSentences.count()

        binding.edtTranscription.hint = if (cursor.currentLetter.letterReadsAsSpells) resources.getString(R.string.txt_translate_sentence_hint) else resources.getString(R.string.txt_translate_sentence_hint_as_reads)



        binding.edtTranscription.addTextChangedListener(object : TextWatcherAdapter {
            override fun afterTextChanged(s: Editable) {
                binding.btnCheck.isEnabled = s.isNotBlank()
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

        /*
        // DEBUG Longest sentence first for layout check
        val longestSentence = GeorgianAlphabet.lettersLearnOrdered.flatMap { it.sentences }.maxBy { it.length }
        binding.txtSentence.text = longestSentence!!.toKhucuri()
        //binding.txtSentence.text = "აქა აკურთხევდით ა ბ გ დ ე ვ ზ თ ი კ ლ მ ნ ო პ ჟ რ ს ტ უ ფ ქ ღ ყ შ ჩ ც ძ წ ჭ ხ ჯ ჰ".toKhucuri(withCapital = true)
        */

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

        val isCorrect = (binding.edtTranscription.text.toString().toSpaceNormalized() == cursor.currentSentence.toReadsAs())

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

        if (cursor.sentenceMoveNext()) {
            showSentenceToTranscript()
            switchControlsState(true)
        } else {
            // Navigation is done to action instead of fragment (R.id.frg_result) to allow back-stack directly to the home
            val action = TranscriptFragmentDirections.actionFrgTranscriptToFrgResult(
                transcriptedCorrectCount,
                transcriptedWrongCount
            )
            view.findNavController().navigate(action)
        }
    }

    private fun showSentenceToTranscript() {

        binding.pbTranscriptProgress.progress = cursor.currentSentencesProgress
        binding.txtTranscriptProgress.text = "${cursor.currentSentencesProgress} / ${cursor.currentSentencesCount}"

        val alwaysAllCaps = AppSettings.isAllCaps
        val nowAllCaps = (kotlin.random.Random.nextInt(5) == 0) // Probability of 20%
        binding.txtSentence.text = cursor.currentSentence.toKhucuri(alwaysAllCaps || nowAllCaps)
    }

    private fun switchControlsState(newSentence: Boolean) {

        with (binding) {
            // New sentence or Check sentence
            if (newSentence) {
                edtTranscription.isFocusableInTouchMode = true
                edtTranscription.isFocusable = true
                edtTranscription.text.clear()
                edtTranscription.requestFocus()

            } else {
                edtTranscription.isFocusableInTouchMode = false
                edtTranscription.isFocusable = false
            }

            if (newSentence) {
                txtBannerCorrect.visibility = View.GONE
                txtBannerWrong.visibility = View.GONE
                btnCheck.isEnabled = false
            }

            btnCheck.visibility = if (newSentence) View.VISIBLE else View.GONE
            btnContinue.visibility = if (newSentence) View.GONE else View.VISIBLE
        }
    }

}
