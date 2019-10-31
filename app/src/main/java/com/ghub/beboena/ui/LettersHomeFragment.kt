package com.ghub.beboena.ui

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager.widget.ViewPager
import com.ghub.beboena.R
import com.ghub.beboena.bl.GeorgianAlphabet
import com.ghub.beboena.ui.view.SlidingTabLayout
import com.ghub.beboena.customview.LettersPagerAdapter
import kotlinx.android.synthetic.main.fragment_home_letters.*

//region OnFragmentInteractionListener pattern

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

//endregion

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [LettersHomeFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [LettersHomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class LettersHomeFragment : Fragment() {

//region OnFragmentInteractionListener pattern
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
//endregion

    init {
        println("Fragment init") // Only happens when navigating to fragment, but not after back is pressed
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return view ?: inflater.inflate(R.layout.fragment_home_letters, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Get the ViewPager and set it's PagerAdapter so that it can display items.
        val letterViewPager = view.findViewById<View>(R.id.view_pager) as ViewPager
        letterViewPager.adapter = LettersPagerAdapter()

        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had it's PagerAdapter set.
        val lettersSlidingTabLayout = view.findViewById<View>(R.id.sliding_tab_layout) as SlidingTabLayout
        lettersSlidingTabLayout.setViewPager(letterViewPager)

        lettersSlidingTabLayout.setOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                GeorgianAlphabet.Cursor.setCurrentPosition(position)

                btn_next_letter.visibility = if (position == 0) View.VISIBLE else View.GONE
                btn_start_exercise.visibility = if (position != 0) View.VISIBLE else View.GONE
            }
        })

        btn_start_exercise.setOnClickListener {
            view.findNavController()
                .navigate(LettersHomeFragmentDirections.actionFrgHomeLettersToFrgTranscript())
            // Navigation.createNavigateOnClickListener(R.id.frg_dest_transcript, null)
        }

        btn_next_letter.setOnClickListener {
            lettersSlidingTabLayout.scrollToPage(1) //TODO add logic of position+1
        }

        lettersSlidingTabLayout.scrollToPage(GeorgianAlphabet.Cursor.getCurrentPosition())
    }


//region OnFragmentInteractionListener pattern

    // to do - Rename method, update argument and hook method into UI event
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
        // to do - Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LettersHomeFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LettersHomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    //endregion

}

