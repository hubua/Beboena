package com.hubua.beboena.utils

import android.text.Editable
import android.text.TextWatcher

interface TextWatcherAdapter : TextWatcher {
    override fun beforeTextChanged(var1: CharSequence, var2: Int, var3: Int, var4: Int) = Unit
    override fun onTextChanged(var1: CharSequence, var2: Int, var3: Int, var4: Int) = Unit
    override fun afterTextChanged(var1: Editable) = Unit
}

/*
    This interface is used to avoid declaring the empty methods which are not used:

        edt_transcription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                btn_check.isEnabled = !s.isBlank()
            }
        })

 */