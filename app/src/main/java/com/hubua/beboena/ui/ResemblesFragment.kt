package com.hubua.beboena.ui

import android.graphics.Typeface
import android.media.MediaPlayer
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableRow
import androidx.core.view.setMargins
import androidx.fragment.app.Fragment
import com.hubua.beboena.R
import com.hubua.beboena.bl.GeorgianAlphabet
import com.hubua.beboena.databinding.FragmentResemblesBinding

/**
 * A simple [Fragment] subclass.
 */
class ResemblesFragment : Fragment() {

    private val cursor = GeorgianAlphabet.Cursor

    private var transcriptedCorrectCount: Int = 0
    private var transcriptedWrongCount: Int = 0

    private var mediaPlayerCorrect: MediaPlayer? = null
    private var mediaPlayerWrong: MediaPlayer? = null

    private var _binding: FragmentResemblesBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView.
    private val bindingExists get() = _binding != null

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

        val rowParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT)
        val btnParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
        btnParams.setMargins((requireContext().resources.displayMetrics.density * 16).toInt()) // Convert DP to PX
        btnParams.weight = 0.5F

        val btn1 = Button(activity, null, 0, R.style.ResemblesButton)
        val btn2 = Button(activity, null, 0, R.style.ResemblesButton)
        btn1.layoutParams = btnParams
        btn2.layoutParams = btnParams

        btn1.text = "hello1"
        btn2.text = "h2"
        val row = TableRow(activity)
        row.layoutParams = rowParams
        row.addView(btn1)
        row.addView(btn2)

        binding.tablePairs.addView(row)

        val btns = ArrayList<Button>()
        btns.add(btn1)
        btns.add(btn2)

        btns.forEach {
            it.setOnClickListener(btnPairClickListener)
        }



        binding.button1.setOnClickListener {
            it.isSelected = !it.isSelected

            it.setBackgroundResource(R.drawable.btn_rounded_background)

        }


        mediaPlayerCorrect = MediaPlayer.create(context, R.raw.answer_correct)
        mediaPlayerWrong = MediaPlayer.create(context, R.raw.answer_wrong)

    }

    private val btnPairClickListener = View.OnClickListener { button ->
        button.isSelected = !button.isSelected
    }




}
