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
import com.hubua.beboena.bl.Analytics
import com.hubua.beboena.bl.AppSettings
import com.hubua.beboena.bl.GeorgianAlphabet
import com.hubua.beboena.databinding.FragmentResultBinding
import com.kobakei.ratethisapp.RateThisApp
import kotlin.math.roundToLong


/**
 * A simple [Fragment] subclass.
 *
 */
class ResultFragment : Fragment(), MediaPlayer.OnPreparedListener {

    private val args: ResultFragmentArgs by navArgs()

    private val correctCount get() = args.transcriptedCorrectCount
    private val incorrectCount get() = args.transcriptedWrongCount

    private var mediaPlayer: MediaPlayer? = null

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView.

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView() //TODO Move to last line
        _binding = null

        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val config = RateThisApp.Config(7, 10)
        config.setTitle(R.string.rate_dialog_title)
        config.setMessage(R.string.rate_dialog_message)
        config.setYesButtonText(R.string.rate_dialog_ok)
        config.setNoButtonText(R.string.rate_dialog_no)
        config.setCancelButtonText(R.string.rate_dialog_cancel)
        RateThisApp.init(config)
        RateThisApp.onCreate(context) // Increments launch times -------------------------

        val soundResId: Int
        val analyticsScore: Char
        binding.btnTryAgain.visibility = View.GONE // Show when Satisfactory (Good/Nice) or Poor ------------------------
        binding.btnNextLetter.visibility = View.GONE // Show when Excellent or Satisfactory
        binding.btnPrevLetter.visibility = View.GONE // Show when Poor or Fail

        when { //-------------------------------------------
            incorrectCount == 0 -> {
                binding.txtResult.text = resources.getString(R.string.txt_result_excellent)
                binding.imgSmiley.setImageResource(R.drawable.smile_excellent)
                soundResId = R.raw.result_excellent_1
                analyticsScore = 'A'

                binding.btnNextLetter.visibility = View.VISIBLE
            }
            correctCount == 0 -> {
                binding.txtResult.text = resources.getString(R.string.txt_result_fail)
                binding.imgSmiley.setImageResource(R.drawable.smile_fail)
                soundResId = R.raw.result_fail
                analyticsScore = 'D'

                binding.btnPrevLetter.visibility = View.VISIBLE
            }
            correctCount > incorrectCount -> {
                binding.txtResult.text = resources.getString(R.string.txt_result_satisfactory)
                binding.imgSmiley.setImageResource(R.drawable.smile_satisfactory)
                soundResId = R.raw.result_satisfactory
                analyticsScore = 'B'

                binding.btnTryAgain.visibility = View.VISIBLE
                binding.btnNextLetter.visibility = View.VISIBLE
            }
            else -> { // correctCount <= incorrectCount
                binding.txtResult.text = resources.getString(R.string.txt_result_poor)
                binding.imgSmiley.setImageResource(R.drawable.smile_poor)
                soundResId = R.raw.result_poor
                analyticsScore = 'C'

                binding.btnTryAgain.visibility = View.VISIBLE
                binding.btnPrevLetter.visibility = View.VISIBLE
            }
        }

        binding.txtResultDetails.text = String.format(resources.getString(R.string.txt_correct_count), correctCount + incorrectCount, correctCount)

        binding.btnTryAgain.setOnClickListener {

            GeorgianAlphabet.Cursor.letterTryAgain()

            view.findNavController().navigate(
                ResultFragmentDirections.actionFrgResultToFrgHomeLetters()
            )

            Analytics.logLevelEnd(Analytics.LevelName.TRANSCRIPT, "TRY_AGAIN")
        }

        binding.btnNextLetter.setOnClickListener {

            RateThisApp.showRateDialogIfNeeded(context, R.style.DialogTheme)

            GeorgianAlphabet.Cursor.letterMoveNext()

            view.findNavController().navigate(
                ResultFragmentDirections.actionFrgResultToFrgHomeLetters()
            )

            Analytics.logLevelEnd(Analytics.LevelName.TRANSCRIPT, "MOVE_NEXT")
        }

        binding.btnPrevLetter.setOnClickListener {

            GeorgianAlphabet.Cursor.letterMovePrev()

            view.findNavController().navigate(
                ResultFragmentDirections.actionFrgResultToFrgHomeLetters()
            )

            Analytics.logLevelEnd(Analytics.LevelName.TRANSCRIPT, "MOVE_PREV")
        }

        if (AppSettings.isEnableSounds) {
            mediaPlayer = MediaPlayer()
            val afd = requireContext().resources.openRawResourceFd(soundResId)
            mediaPlayer?.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()
            mediaPlayer?.setOnPreparedListener(this@ResultFragment)
            mediaPlayer?.prepareAsync() // prepare async to not block main UI thread
        }

        Analytics.logScreenView(ResultFragment::class.simpleName!!)
        Analytics.logPostScore(analyticsScore.toString(), (correctCount * 100f / (correctCount + incorrectCount)).roundToLong())
    }

    override fun onPrepared(mediaPlayer: MediaPlayer?) {
        mediaPlayer?.start()
    }


}