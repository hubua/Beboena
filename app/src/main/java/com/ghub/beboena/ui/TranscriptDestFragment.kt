package com.ghub.beboena.ui

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.navigation.fragment.navArgs
import com.ghub.beboena.R
import com.ghub.beboena.bl.GeorgianAlphabet
import com.ghub.beboena.bl.GeorgianLetter
import com.ghub.beboena.bl.toChar
import com.ghub.beboena.bl.toKhucuri
import kotlinx.android.synthetic.main.fragment_dest_transcript.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [TranscriptDestFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [TranscriptDestFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class TranscriptDestFragment : androidx.fragment.app.Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    private val args: TranscriptDestFragmentArgs by navArgs()

    private val currentLetter get() = GeorgianAlphabet.lettersById[args.letterId.toChar()]!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dest_transcript, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        btn_check.setOnClickListener { view -> onBtnCheckClick(view) }

        val txtCurrentLetter: TextView = view.findViewById(R.id.txt_current_letter)

        val spannable = SpannableString(String.format(resources.getString(R.string.txt_learning_letter), currentLetter.letterId))
        spannable.setSpan(ForegroundColorSpan(Color.MAGENTA), 14, 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        txtCurrentLetter.text = spannable

        pb_transcript_progress.max = currentLetter.sentences.count()
        pb_transcript_progress.progress = 1
        txt_transcript_progress.text = "1 / ${currentLetter.sentences.count()}"
        txt_sentence.text = currentLetter.sentences[0].toKhucuri()
    }

    private fun onBtnCheckClick(view: View)
    {
        txt_current_letter.text = currentLetter.mkhedruli.toString()
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TranscriptDestFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TranscriptDestFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
