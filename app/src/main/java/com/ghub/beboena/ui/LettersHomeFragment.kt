package com.ghub.beboena.ui

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ghub.beboena.R
import com.ghub.beboena.ui.view.SlidingTabLayout
import com.ghub.beboena.view.LettersPagerAdapter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

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
    // TODO: Rename and change types of parameters
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_letters, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // A [ViewPager] which will be used in conjunction with the [SlidingTabLayout] below.
        // Get the ViewPager and set it's PagerAdapter so that it can display items.
        val lettersViewPager = view.findViewById<View>(R.id.view_pager) as ViewPager
        lettersViewPager!!.adapter = LettersPagerAdapter()

        // A custom [ViewPager] title strip which looks much like Tabs present in Android v4.0 and
        // above, but is designed to give continuous feedback to the user when scrolling.
        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had it's PagerAdapter set.
        val lettersSlidingTabLayout = view.findViewById<View>(R.id.sliding_tab_layout) as SlidingTabLayout
        lettersSlidingTabLayout!!.setViewPager(lettersViewPager)
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
         * @return A new instance of fragment LettersHomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LettersHomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

