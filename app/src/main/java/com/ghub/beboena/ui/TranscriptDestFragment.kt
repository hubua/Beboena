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
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.navigation.fragment.navArgs
import com.ghub.beboena.R
import com.ghub.beboena.bl.GeorgianAlphabet
import com.ghub.beboena.bl.GeorgianLetter
import com.ghub.beboena.bl.toChar
import com.ghub.beboena.bl.toKhucuri
import com.ghub.beboena.utils.KeyboardUtils
import es.dmoral.toasty.Toasty
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

    private var currentSentenceIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dest_transcript, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        btn_check.setOnClickListener { view -> onBtnCheckClick(view) }

        val txtCurrentLetter: TextView = view.findViewById(R.id.txt_current_letter)

        val spannable = SpannableString(String.format(resources.getString(R.string.txt_learning_letter), currentLetter.letterId))
        spannable.setSpan(ForegroundColorSpan(Color.MAGENTA), 14, 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        txtCurrentLetter.text = spannable

        displaySentenceAndProgress()

        btn_next_sentence.isEnabled = false

        edt_transcription.setOnFocusChangeListener { view, hasFocus -> onFocusChangeListener(view, hasFocus) }
    }

    private fun onFocusChangeListener(view: View, hasFocus: Boolean){
        edt_transcription.isFocusableInTouchMode = false
        edt_transcription.isFocusable = false
    }

    private fun onBtnCheckClick(view: View) {

        if (!edt_transcription.text.toString().isBlank()) {

            if (edt_transcription.text.toString().equals(currentLetter.sentences[currentSentenceIndex])) {
                Toasty.success(context!!, R.string.toast_correct, Toast.LENGTH_SHORT, true).show();
            } else {
                Toasty.error(context!!, R.string.toast_wrong, Toast.LENGTH_SHORT, true).show();
            }
            KeyboardUtils.hideKeyboard(this.activity!!)

            currentSentenceIndex++
            displaySentenceAndProgress()

        } else {
            Toasty.info(context!!, R.string.toast_blank, Toast.LENGTH_SHORT, true).show();
        }

    }

    private fun displaySentenceAndProgress() {
        pb_transcript_progress.max = currentLetter.sentences.count()
        pb_transcript_progress.progress = currentSentenceIndex + 1
        txt_transcript_progress.text = "${currentSentenceIndex + 1} / ${currentLetter.sentences.count()}"
        txt_sentence.text = currentLetter.sentences[currentSentenceIndex].toKhucuri()
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
