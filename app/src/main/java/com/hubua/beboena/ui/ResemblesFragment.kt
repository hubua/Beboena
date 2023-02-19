package com.hubua.beboena.ui

import android.graphics.Typeface
import android.media.MediaPlayer
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import androidx.core.view.setMargins
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.hubua.beboena.R
import com.hubua.beboena.bl.GeorgianAlphabet
import com.hubua.beboena.databinding.FragmentResemblesBinding
import java.util.concurrent.atomic.AtomicBoolean


/**
 * A simple [Fragment] subclass.
 */
class ResemblesFragment : Fragment() {

    private val cursor = GeorgianAlphabet.Cursor

    private var mediaPlayerCorrect: MediaPlayer? = null
    private var mediaPlayerWrong: MediaPlayer? = null

    private var _binding: FragmentResemblesBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView.
    private val bindingExists get() = _binding != null //TODO need this?

    private val _btnsL: MutableList<Button> = mutableListOf()
    private val _btnsR: MutableList<Button> = mutableListOf()

    private var matchedCorrectCount: Int = 0
    private var matchedWrongCount: Int = 0
    private val matchPass get() = matchedCorrectCount == cursor.currentPairs.count() && matchedWrongCount < 3

    private var _isBtnClickSuspended = AtomicBoolean(false) // Allows reference wrapper over value

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentResemblesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        mediaPlayerCorrect?.release()
        mediaPlayerCorrect = null
        mediaPlayerWrong?.release()
        mediaPlayerWrong = null

        _binding = null
        super.onDestroyView()
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

        binding.tablePairs.removeAllViews()

        for (item in cursor.currentPairs) {

            val layoutParamsRow = TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 0, 1.0F)
            val layoutParamsBtn = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 0.5F)
            val px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, resources.getDimension(R.dimen.resembles_btn_paris_margin), resources.displayMetrics).toInt() // Convert DP to PX
            layoutParamsBtn.setMargins(px)

            val row = TableRow(activity)
            row.layoutParams = layoutParamsRow
            binding.tablePairs.addView(row)

            val btnL = Button(activity, null, 0, R.style.ResemblesButtonL)
            val btnR = Button(activity, null, 0, R.style.ResemblesButtonR)

            (listOf(btnL, btnR)).forEach {
                it.setOnClickListener(btnMatchPairClick)
                it.layoutParams = layoutParamsBtn
                row.addView(it)
            }

            _btnsL.add(btnL)
            _btnsR.add(btnR)

        }

        binding.btnContinue.setOnClickListener { onBtnContinueClick(it) } // Is equivalent to 'btn -> onBtnContinueClick(btn)'

        showPairsToMatch()

        mediaPlayerCorrect = MediaPlayer.create(context, R.raw.answer_correct)
        mediaPlayerWrong = MediaPlayer.create(context, R.raw.answer_wrong)

    }

    private val btnMatchPairClick = View.OnClickListener { button ->

        if (_isBtnClickSuspended.get())
            return@OnClickListener

        val selectedBtnL = _btnsL.singleOrNull() { it.isSelected && it !== button }
        val selectedBtnR = _btnsR.singleOrNull() { it.isSelected && it !== button }

        if (selectedBtnL == null && selectedBtnR == null) {
            button.isSelected = !button.isSelected
        } else {
            val clickedBtnL = _btnsL.singleOrNull() { it === button }
            val clickedBtnR = _btnsR.singleOrNull() { it === button }

            if (clickedBtnL != null && selectedBtnL != null) {
                // Selecting another button in L
                selectedBtnL.isSelected = false
                clickedBtnL.isSelected = true
            } else if (clickedBtnR != null && selectedBtnR != null) {
                // Selecting another button in R
                selectedBtnR.isSelected = false
                clickedBtnR.isSelected = true
            } else {

                val selectedBtn = selectedBtnL ?: selectedBtnR
                val clickedBtn = clickedBtnL ?: clickedBtnR

                if (selectedBtn!!.tag == clickedBtn!!.tag) {
                    // Correct answer

                    matchedCorrectCount++

                    selectedBtn.isSelected = false
                    clickedBtn.isSelected = false
                    selectedBtn.isEnabled = false
                    clickedBtn.isEnabled = false

                    selectedBtn.setBackgroundResource(R.drawable.btn_resembles_background_correct)
                    clickedBtn.setBackgroundResource(R.drawable.btn_resembles_background_correct)
                } else {
                    // Wrong answer

                    matchedWrongCount++

                    val anim = AlphaAnimation(0.2F, 1.0F)
                    anim.duration = 700
                    anim.repeatCount = 2
                    //anim.repeatMode = Animation.REVERSE
                    //anim.startOffset = 1000

                    anim.setAnimationListener(ButtonAnimationListener(clickedBtn, selectedBtn, _isBtnClickSuspended))
                    clickedBtn.startAnimation(anim)

                }

            }
        }
    }

    private fun onBtnContinueClick(view: View) {

        view.findNavController().navigate(
            if (matchPass) {
                ResemblesFragmentDirections.actionFrgResemblesToFrgTranscript() // return result
            } else {
                cursor.letterTryAgain()
                ResemblesFragmentDirections.actionFrgResemblesToFrgResembles()  // return result
            }
        )
    }

    private fun showPairsToMatch() {

        val pairsCharL = cursor.currentPairs.withIndex()
        val pairsCharR = cursor.currentPairs.shuffled()

        for ((index, charL) in pairsCharL) {
            val btnL = _btnsL[index]
            val btnR = _btnsR[index]

            btnL.text = GeorgianAlphabet.lettersMap[charL]!!.nuskhuri.toString()
            btnL.tag = GeorgianAlphabet.lettersMap[charL]!!.letterModernSpelling

            btnR.text = GeorgianAlphabet.lettersMap[pairsCharR[index]]!!.mkhedruli.toString()
            btnR.tag = GeorgianAlphabet.lettersMap[pairsCharR[index]]!!.letterModernSpelling
        }
    }

    class ButtonAnimationListener(selectedBtn: Button, clickedBtn: Button, flag: AtomicBoolean) : AnimationListener {
        private val _selectedBtn = selectedBtn
        private val _clickedBtn = clickedBtn
        private val _flag = flag

        override fun onAnimationStart(animation: Animation) {
            _selectedBtn.isEnabled = false
            _clickedBtn.isEnabled = false
            _flag.set(true)
        }

        override fun onAnimationEnd(animation: Animation) {
            _selectedBtn.isEnabled = true
            _clickedBtn.isEnabled = true
            _flag.set(false)
        }

        override fun onAnimationRepeat(animation: Animation) {}
    }


}
