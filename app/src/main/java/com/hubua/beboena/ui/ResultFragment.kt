package com.hubua.beboena.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.hubua.beboena.R
import com.hubua.beboena.bl.AppSettings
import com.hubua.beboena.bl.GeorgianAlphabet
import kotlinx.android.synthetic.main.fragment_result.*


/**
 * A simple [Fragment] subclass.
 *
 */
class ResultFragment : Fragment(), MediaPlayer.OnPreparedListener {

    private val args: ResultFragmentArgs by navArgs()

    private val correctCount get() = args.transcriptedCorrectCount
    private val incorrectCount get() = args.transcriptedWrongCount

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        var soundResId = -1;
        when {
            correctCount == 0 -> {
                txt_result.text = resources.getString(R.string.txt_result_bad)
                img_smiley.setImageResource(R.drawable.smile_bad)
                soundResId = R.raw.result_bad
            }
            incorrectCount == 0 -> {
                txt_result.text = resources.getString(R.string.txt_result_excellent)
                img_smiley.setImageResource(R.drawable.smile_excellent)
                soundResId = R.raw.result_excellent_1
            }
            correctCount < incorrectCount -> {
                txt_result.text = resources.getString(R.string.txt_result_unsatisfactory)
                img_smiley.setImageResource(R.drawable.smile_unsatisfactory)
                soundResId = R.raw.result_unsatisfactory
            }
            else -> { // correctCount >= incorrectCount
                txt_result.text = resources.getString(R.string.txt_result_satisfactory)
                img_smiley.setImageResource(R.drawable.smile_satisfactory)
                soundResId = R.raw.result_good
            }
        }

        txt_result_details.text = String.format(resources.getString(R.string.txt_correct_count), correctCount + incorrectCount, correctCount)

        btn_try_again.visibility = if (incorrectCount != 0) View.VISIBLE else View.GONE // Has incorrect answers
        btn_try_again.setOnClickListener {

            view.findNavController().navigate(
                ResultFragmentDirections.actionFrgResultToFrgHomeLetters()
            )
        }

        btn_next_letter.visibility = if (correctCount != 0) View.VISIBLE else View.GONE // Has correct answers
        btn_next_letter.setOnClickListener {

            GeorgianAlphabet.Cursor.moveNext()

            view.findNavController().navigate(
                ResultFragmentDirections.actionFrgResultToFrgHomeLetters()
            )
        }

        if (AppSettings.isEnableSounds) {
            mediaPlayer = MediaPlayer()
            val afd = context!!.resources.openRawResourceFd(soundResId)
            mediaPlayer?.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()
            mediaPlayer?.setOnPreparedListener(this@ResultFragment)
            mediaPlayer?.prepareAsync() // prepare async to not block main UI thread
        }
    }

    override fun onPrepared(mediaPlayer: MediaPlayer?) {
        mediaPlayer?.start()
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
    }

}