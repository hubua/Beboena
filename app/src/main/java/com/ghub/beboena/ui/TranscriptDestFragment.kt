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
import androidx.navigation.fragment.navArgs
import com.ghub.beboena.R
import com.ghub.beboena.bl.GeorgianAlphabet
import com.ghub.beboena.bl.toChar
import com.ghub.beboena.bl.toKhucuri
import com.ghub.beboena.utils.KeyboardUtils
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_dest_transcript.*

//region OnFragmentInteractionListener pattern

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

//endregion

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

    //region OnFragmentInteractionListener pattern

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    //endregion

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

        val txtCurrentLetter: TextView = view.findViewById(R.id.txt_current_letter)
        val spannable = SpannableString(String.format(resources.getString(R.string.txt_learning_letter), currentLetter.letterId))
        spannable.setSpan(ForegroundColorSpan(Color.MAGENTA), 14, 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        txtCurrentLetter.text = spannable

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

        txt_correct.visibility = View.INVISIBLE
        txt_wrong.visibility = View.GONE

        txt_correct.animate()
            .translationX(0.0f)
            .alpha(0.0f)

        btn_check.isEnabled = false

        btn_check.visibility = View.VISIBLE
        btn_check.setOnClickListener { view -> onBtnCheckClick(view) }

        btn_continue.visibility = View.GONE
        btn_continue.setOnClickListener { view -> onBtnContinueClick(view) }

        displaySentenceAndProgress(currentSentenceIndex)
    }

    private fun onBtnCheckClick(view: View) {

        if (!edt_transcription.text.toString().isBlank()) {

            if (edt_transcription.text.toString().equals(currentLetter.sentences[currentSentenceIndex])) {
                //Toasty.success(context!!, R.string.txt_correct, Toast.LENGTH_SHORT, true).show();
            } else {
                //Toasty.error(context!!, R.string.txt_wrong, Toast.LENGTH_SHORT, true).show();
            }
            KeyboardUtils.hideKeyboard(this.activity!!)

            edt_transcription.text.clear()

            btn_check.visibility = View.GONE
            btn_continue.visibility = View.VISIBLE

            edt_transcription.isFocusableInTouchMode = false
            edt_transcription.isFocusable = false


            txt_correct.setVisibility(View.VISIBLE);
            txt_correct.setAlpha(0.0f);

            txt_correct.animate()
                .translationX(txt_correct.getWidth().toFloat())
                .alpha(1.0f)
                .setDuration(1000)
                .setListener(null);

        } else {
            Toasty.info(context!!, R.string.txt_blank, Toast.LENGTH_SHORT, true).show();
        }

    }

    private fun onBtnContinueClick(view: View) {
        btn_check.visibility = View.VISIBLE
        btn_continue.visibility = View.GONE

        edt_transcription.isFocusableInTouchMode = true
        edt_transcription.isFocusable = true

        if (currentSentenceIndex + 1 < currentLetter.sentences.count()) {
            displaySentenceAndProgress(++currentSentenceIndex)
        }
    }

    private fun displaySentenceAndProgress(sentenceIndex: Int) {
        pb_transcript_progress.max = currentLetter.sentences.count()
        pb_transcript_progress.progress = sentenceIndex + 1
        txt_transcript_progress.text = "${sentenceIndex + 1} / ${currentLetter.sentences.count()}"
        txt_sentence.text = currentLetter.sentences[sentenceIndex].toKhucuri()
    }

    //region OnFragmentInteractionListener pattern

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

    //endregion
}
