package com.ghub.beboena.customview

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ghub.beboena.R
import com.ghub.beboena.bl.GeorgianAlphabet

internal class LettersPagerAdapter : androidx.viewpager.widget.PagerAdapter() {

    private val lettersByPosition = GeorgianAlphabet.lettersByOrderIndex

    /**
     * @return the number of pages to display
     */
    override fun getCount(): Int {
        return lettersByPosition.count()
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
        val letter = lettersByPosition[position]
        return "${letter.asomtavruli} ${letter.nuskhuri} (${letter.name})"
    }

    /**
     * Instantiate the [View] which should be displayed at `position`. Here we
     * inflate a layout from the apps resources and then change the text view to signify the position.
     */
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val letter = lettersByPosition[position]

        val view = LayoutInflater.from(container.context).inflate(R.layout.pager_item_letter, container, false)
        container.addView(view)

        val txtLetterMkhedruli = view.findViewById(R.id.txtLetterMkhedruli) as TextView //TODO use synthetic binding
        txtLetterMkhedruli.text = "${letter.mkhedruli.toString()} - \"${letter.name}\""

        val txtAsomtavruli = view.findViewById(R.id.txtAsomtavruliLabel) as TextView
        txtAsomtavruli.text = Html.fromHtml(container.context.resources.getString(R.string.txt_asomtavruli_label), Html.FROM_HTML_MODE_LEGACY)

        val txtNuskhuri = view.findViewById(R.id.txtNuskhuriLabel) as TextView
        txtNuskhuri.text = Html.fromHtml(container.context.resources.getString(R.string.txt_nuskhuri_label), Html.FROM_HTML_MODE_LEGACY)

        val txtAsmtBech = view.findViewById(R.id.txtAsmtBech) as TextView
        txtAsmtBech.text = letter.asomtavruli.toString()

        val txtAsmtKhel = view.findViewById(R.id.txtAsmtKhel) as TextView
        txtAsmtKhel.text = " ${letter.asomtavruli}"

        val txtNskhBech = view.findViewById(R.id.txtNskhBech) as TextView
        txtNskhBech.text = letter.nuskhuri.toString()

        val txtNskhKhel = view.findViewById(R.id.txtNskhKhel) as TextView
        txtNskhKhel.text = " ${letter.nuskhuri}"

        return view
    }

    /**
     * Destroy the item from the [ViewPager]. In our case this is simply removing the [View].
     */
    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }

}