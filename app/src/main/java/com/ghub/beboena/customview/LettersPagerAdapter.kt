package com.ghub.beboena.customview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ghub.beboena.R
import com.ghub.beboena.bl.GeorgianAlphabet
import kotlinx.android.synthetic.main.pager_item_letter.view.*

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
        val letter = lettersOrdered[position]

        val view = LayoutInflater.from(container.context).inflate(R.layout.pager_item_letter, container, false)
        container.addView(view)

        val read = if (letter.read.isEmpty()) "" else " \"${letter.read}\""
        view.txtLetterMkhedruli.text = "${letter.name} - ${letter.mkhedruli}${read}"

        view.txtBechduri.text = "${letter.asomtavruli} ${letter.nuskhuri}"
        view.txtKhelnatseri.text = "${letter.asomtavruli} ${letter.nuskhuri}"

        return view
    }

    /**
     * Destroy the item from the [ViewPager]. In our case this is simply removing the [View].
     */
    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }

}