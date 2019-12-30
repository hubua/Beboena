package com.hubua.beboena.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.hubua.beboena.R
import kotlinx.android.synthetic.main.dialog_fragment_references.*


class ReferencesDialogFragment : DialogFragment() {

    /** The system calls this to get the DialogFragment's layout, regardless
    of whether it's being displayed as a dialog or an embedded fragment. */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout to use as dialog or embedded fragment
        return inflater.inflate(R.layout.dialog_fragment_references, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //val unencodedHtml =
        //    "&lt;html&gt;&lt;body&gt;'%23' is the percent code for ‘#‘ &lt;/body&gt;&lt;/html&gt;"
        //val encodedHtml = Base64.encodeToString(unencodedHtml.toByteArray(), Base64.NO_PADDING)

        val s = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Nulla aliquet enim tortor at auctor urna. Nec ultrices dui sapien eget. Non arcu risus quis varius quam quisque. Donec adipiscing tristique risus nec feugiat. Velit dignissim sodales ut eu sem integer vitae justo eget. Metus aliquam eleifend mi in nulla posuere sollicitudin aliquam. A arcu cursus vitae congue mauris rhoncus aenean vel. Felis donec et odio pellentesque diam volutpat commodo sed egestas. Tempor orci eu lobortis elementum nibh tellus molestie nunc non. Amet facilisis magna etiam tempor orci eu lobortis. Morbi tempus iaculis urna id volutpat lacus laoreet non curabitur. Morbi tristique senectus et netus et. Donec et odio pellentesque diam volutpat commodo. Felis imperdiet proin fermentum leo. Diam vulputate ut pharetra sit. Et tortor at risus viverra adipiscing at in.\n" +
                "\n" +
                "Diam quis enim lobortis scelerisque fermentum dui faucibus. Quis vel eros donec ac odio tempor orci. Amet porttitor eget dolor morbi non arcu. Vitae suscipit tellus mauris a. Nisi quis eleifend quam adipiscing vitae proin sagittis nisl. Scelerisque varius morbi enim nunc faucibus. Nibh tellus molestie nunc non blandit massa enim nec dui. Ac turpis egestas maecenas pharetra. Ac turpis egestas integer eget aliquet nibh praesent tristique magna. Morbi tristique senectus et netus et malesuada fames.\n" +
                "\n" +
                "Suspendisse potenti nullam ac tortor. Duis convallis convallis tellus id interdum. Eros donec ac odio tempor orci. Habitant morbi tristique senectus et netus et. Phasellus egestas tellus rutrum tellus pellentesque eu. Faucibus vitae aliquet nec ullamcorper sit amet. Libero nunc consequat interdum varius. Lectus magna fringilla urna porttitor rhoncus dolor purus. Sed felis eget velit aliquet sagittis id. Posuere morbi leo urna molestie at elementum eu facilisis sed. Tristique et egestas quis ipsum suspendisse ultrices gravida dictum. Semper risus in hendrerit gravida rutrum quisque non tellus orci. Ultrices dui sapien eget mi proin sed libero enim."

        web_view.loadData(s, "text/html", "text")

        //textView2.text = s
        //textView2.movementMethod = ScrollingMovementMethod()
    }

    /** The system calls this only when creating the layout in a dialog. */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }
}