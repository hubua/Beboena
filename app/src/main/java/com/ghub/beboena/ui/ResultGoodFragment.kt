package com.ghub.beboena.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.ghub.beboena.R

/**
 * A simple [Fragment] subclass.
 *
 */
class ResultGoodFragment : Fragment() {

    private val args: ResultGoodFragmentArgs by navArgs()

    private val transcriptedCorrectCount = args.transcriptedCorrectCount
    private val transcriptedWrongCount = args.transcriptedWrongCount

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_resultgood, container, false)
    }

}
