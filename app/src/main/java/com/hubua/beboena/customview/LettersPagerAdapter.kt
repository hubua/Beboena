package com.hubua.beboena.customview

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hubua.beboena.R
import com.hubua.beboena.bl.GeorgianAlphabet
import com.hubua.beboena.databinding.PagerItemLetterBinding

/**
 * A ViewPager which will be used in conjunction with the SlidingTabLayout.
 */
internal class LettersPagerAdapter : androidx.viewpager.widget.PagerAdapter() {

    private val lettersOrdered = GeorgianAlphabet.lettersLearnOrdered

    /**
     * @return the number of pages to display
     */
    override fun getCount(): Int {
        return lettersOrdered.count()
    }

    /**
     * @return true if the value returned from [.instantiateItem] is the
     * same object as the [View] added to the [ViewPager].
     */
    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return obj === view
    }

    /**
     * @return the title of the item at `position`. This is important as what this method
     * returns is what is displayed in the [SlidingTabLayout].
     *
     * Here we construct one using the position value, but for real application the title should
     * refer to the item's contents.
     */
    override fun getPageTitle(position: Int): CharSequence? {
        val letter = lettersOrdered[position]
        return "${letter.asomtavruli} ${letter.nuskhuri} (${letter.mkhedruli})"
    }

    /**
     * Instantiate the [View] which should be displayed at `position`. Here we
     * inflate a layout from the apps resources and then change the text view to signify the position.
     */
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(container.context)
        val binding = PagerItemLetterBinding.inflate(inflater, container, false)
        val view = binding.root

        container.addView(view)

        val letter = lettersOrdered[position]

        binding.txtLetterMkhedruli.text = "${letter.name} - ${letter.mkhedruli}"

        binding.txtLetterReadsAs.visibility = if (!letter.letterReadsAsSpells) View.VISIBLE else View.GONE
        val spannable = SpannableString(String.format(container.resources.getString(R.string.txt_reads_as), letter.letterReadsAs))
        spannable.setSpan(
            StyleSpan(Typeface.BOLD),
            spannable.length - letter.letterReadsAs.length - 2,
            spannable.length - 2,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.txtLetterReadsAs.text = spannable

        binding.txtBechduri.text = "${letter.asomtavruli} ${letter.nuskhuri}"
        binding.txtKhelnatseri.text = "${letter.asomtavruli} ${letter.nuskhuri}"

        return view
    }

    /**
     * Destroy the item from the [ViewPager]. In our case this is simply removing the [View].
     */
    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }

}