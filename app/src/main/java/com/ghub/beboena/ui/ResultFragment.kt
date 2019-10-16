package com.ghub.beboena.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.ghub.beboena.R
import kotlinx.android.synthetic.main.fragment_result.*

/**
 * A simple [Fragment] subclass.
 *
 */
class ResultFragment : Fragment() {

    private val args: ResultFragmentArgs by navArgs()

    private val correctCount get() = args.transcriptedCorrectCount
    private val incorrectCount get() = args.transcriptedWrongCount

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        when {
            correctCount == 0 ->
                txt_result.text = resources.getString(R.string.txt_result_bad)
            incorrectCount == 0 ->
                txt_result.text = resources.getString(R.string.txt_result_excellent)
            correctCount < incorrectCount ->
                txt_result.text = resources.getString(R.string.txt_result_unsatisfactory)
            else -> // correctCount >= incorrectCount
                txt_result.text = resources.getString(R.string.txt_result_satisfactory)
        }

        txt_correct_count.text = String.format(resources.getString(R.string.txt_correct_count), correctCount + incorrectCount, correctCount)
    }

}
