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
import com.kobakei.ratethisapp.RateThisApp
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

        val config = RateThisApp.Config(7, 10)
        config.setTitle(R.string.rate_dialog_title)
        config.setMessage(R.string.rate_dialog_message)
        config.setYesButtonText(R.string.rate_dialog_ok)
        config.setNoButtonText(R.string.rate_dialog_no)
        config.setCancelButtonText(R.string.rate_dialog_cancel)
        RateThisApp.init(config)
        RateThisApp.onCreate(context) // Increments launch times

        var soundResId: Int
        btn_try_again.visibility = View.GONE // Show when Satisfactory (Good) or Poor
        btn_next_letter.visibility = View.GONE // Show when Excellent or Satisfactory
        btn_prev_letter.visibility = View.GONE // Show when Poor or Fail

        when {
            incorrectCount == 0 -> {
                txt_result.text = resources.getString(R.string.txt_result_excellent)
                img_smiley.setImageResource(R.drawable.smile_excellent)
                soundResId = R.raw.result_excellent_1

                btn_next_letter.visibility = View.VISIBLE
            }
            correctCount == 0 -> {
                txt_result.text = resources.getString(R.string.txt_result_fail)
                img_smiley.setImageResource(R.drawable.smile_fail)
                soundResId = R.raw.result_fail

                btn_prev_letter.visibility = View.VISIBLE
            }
            correctCount > incorrectCount -> {
                txt_result.text = resources.getString(R.string.txt_result_satisfactory)
                img_smiley.setImageResource(R.drawable.smile_satisfactory)
                soundResId = R.raw.result_satisfactory

                btn_try_again.visibility = View.VISIBLE
                btn_next_letter.visibility = View.VISIBLE
            }
            else -> { // correctCount <= incorrectCount
                txt_result.text = resources.getString(R.string.txt_result_poor)
                img_smiley.setImageResource(R.drawable.smile_poor)
                soundResId = R.raw.result_poor

                btn_try_again.visibility = View.VISIBLE
                btn_prev_letter.visibility = View.VISIBLE
            }
        }

        txt_result_details.text = String.format(resources.getString(R.string.txt_correct_count), correctCount + incorrectCount, correctCount)

        btn_try_again.setOnClickListener {

            GeorgianAlphabet.Cursor.positionTryAgain()

            view.findNavController().navigate(
                ResultFragmentDirections.actionFrgResultToFrgHomeLetters()
            )
        }

        btn_next_letter.setOnClickListener {

            RateThisApp.showRateDialogIfNeeded(context, R.style.DialogTheme)

            GeorgianAlphabet.Cursor.positionMoveNext()

            view.findNavController().navigate(
                ResultFragmentDirections.actionFrgResultToFrgHomeLetters()
            )
        }

        btn_prev_letter.setOnClickListener {

            GeorgianAlphabet.Cursor.positionMovePrev()

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