package com.ghub.beboena.view

import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ghub.beboena.R
import com.ghub.beboena.bl.GeorgianAlphabet

internal class LettersPagerAdapter : PagerAdapter() {

    private val letters = GeorgianAlphabet.lettersToLearn

    /**
     * @return the number of pages to display
     */
    override fun getCount(): Int {
        return letters.count()
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
        val letter = letters.values.toList()[position]
        return "${letter.asomtavruli} ${letter.nuskhuri} (${letter.name})"
    }

    /**
     * Instantiate the [View] which should be displayed at `position`. Here we
     * inflate a layout from the apps resources and then change the text view to signify the position.
     */
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        // Inflate a new layout from our resources
        val view = LayoutInflater.from(container.context).inflate(R.layout.pager_item_letter, container, false)
        // Add the newly created View to the ViewPager
        container.addView(view)

        // Retrieve a TextView from the inflated View, and update it's text
        val title = view.findViewById(R.id.txt_item_title) as TextView
        title.text = (position + 1).toString()

        // Return the View
        return view
    }

    /**
     * Destroy the item from the [ViewPager]. In our case this is simply removing the [View].
     */
    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }

}