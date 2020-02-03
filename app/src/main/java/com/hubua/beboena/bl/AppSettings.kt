package com.hubua.beboena.bl

import android.content.SharedPreferences

object AppSettings {

    private const val ENABLE_SOUNDS_KEY = "ENABLE_SOUNDS_KEY"

    private lateinit var _pref: SharedPreferences

    var isEnableSounds: Boolean
        get() {
            if (!::_pref.isInitialized) throw IllegalStateException("SharedPreferences not initialized")

            return _pref.getBoolean(ENABLE_SOUNDS_KEY, true)
        }
        set(value) {
            if (!::_pref.isInitialized) throw IllegalStateException("SharedPreferences not initialized")

            with(_pref.edit()) {
                putBoolean(ENABLE_SOUNDS_KEY, value)
                apply()
            }
        }

    fun initialize(sharedPref: SharedPreferences) {
        _pref = sharedPref
    }

}