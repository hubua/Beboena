package com.hubua.beboena.ui

//import android.widget.PopupMenu
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.viewpager.widget.ViewPager
import com.hubua.beboena.R
import com.hubua.beboena.bl.AppSettings
import com.hubua.beboena.bl.GeorgianAlphabet
import com.hubua.beboena.customview.LettersPagerAdapter
import com.hubua.beboena.databinding.FragmentHomeLettersBinding
import com.hubua.beboena.ui.dialogs.ContactDialogFragment
import com.hubua.beboena.ui.dialogs.DefinitionsDialogFragment
import com.hubua.beboena.ui.dialogs.TipsDialogFragment
import com.hubua.beboena.ui.view.SlidingTabLayout


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
        println("Fragment init") // Only happens when navigating to fragment, but not after back is pressed!
    }

    private var _binding: FragmentHomeLettersBinding? = null
    private val binding get() = _binding!! // This property is only valid between onCreateView and onDestroyView.

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeLettersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Get the ViewPager and set it's PagerAdapter so that it can display items.
        val letterViewPager = view.findViewById<View>(R.id.view_pager) as ViewPager
        letterViewPager.adapter = LettersPagerAdapter()

        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had it's PagerAdapter set.
        val lettersSlidingTabLayout = view.findViewById<View>(R.id.sliding_tab_layout) as SlidingTabLayout
        lettersSlidingTabLayout.setViewPager(letterViewPager)

        lettersSlidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(requireContext(), R.color.colorPrimary))

        lettersSlidingTabLayout.scrollToPage(GeorgianAlphabet.Cursor.getCurrentLetterPosition())

        showNextStartButtons()

        lettersSlidingTabLayout.setOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {

                GeorgianAlphabet.Cursor.letterJumpTo(position)

                showNextStartButtons()
            }
        })

        binding.btnFollowingLetter.setOnClickListener {
            val nextPosition = GeorgianAlphabet.Cursor.letterMoveNext()
            lettersSlidingTabLayout.scrollToPage(nextPosition)
        }

        binding.btnStartExercise.setOnClickListener {
            view.findNavController().navigate(
                if (GeorgianAlphabet.Cursor.hasPairs) {
                    LettersHomeFragmentDirections.actionFrgHomeLettersToFrgResembles() // return result
                } else {
                    LettersHomeFragmentDirections.actionFrgHomeLettersToFrgTranscript() // return result
                }
            )
        }

        binding.btnMenu.setOnClickListener { showPopup(it) }
    }

    private fun showNextStartButtons() {
        val hasSentences = GeorgianAlphabet.Cursor.currentLetter.hasSentences
        binding.btnFollowingLetter.visibility = if (!hasSentences) View.VISIBLE else View.GONE
        binding.btnStartExercise.visibility = if (hasSentences) View.VISIBLE else View.GONE
    }

    private fun showPopup(v: View) {
        //TODO add light border, rounded corners and anchor to top of button
        PopupMenu(requireContext(), v).apply {
            setOnMenuItemClickListener { onMenuItemClick(it)}
            inflate(R.menu.nav_menu)
            menu.findItem(R.id.mnu_enable_sounds).isChecked = AppSettings.isEnableSounds
            menu.findItem(R.id.mnu_all_caps).isChecked = AppSettings.isAllCaps
            show()
        }
    }

    private fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.mnu_enable_sounds -> {
                item.isChecked = !item.isChecked
                AppSettings.isEnableSounds = item.isChecked
                true
            }
            R.id.mnu_all_caps -> {
                item.isChecked = !item.isChecked
                AppSettings.isAllCaps = item.isChecked
                true
            }
            R.id.mnu_definitions -> {
                val dialog = DefinitionsDialogFragment()
                dialog.show(parentFragmentManager, "Definitions")
                true
            }
            R.id.mnu_tips -> {
                val dialog = TipsDialogFragment()
                dialog.show(parentFragmentManager, "Tips")
                true
            }
            R.id.mnu_useful_downloads -> {
                val openURL = Intent(Intent.ACTION_VIEW)
                openURL.data = Uri.parse("http://beboena.me/downloads/")
                startActivity(openURL)
                true
            }
            R.id.mnu_contact -> {
                val dialog = ContactDialogFragment()
                dialog.show(parentFragmentManager, "Contact")
                true
            }
            else -> false
        }
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

