package com.hubua.beboena.bl

import android.content.SharedPreferences

object AppSettings {

    private const val ENABLE_SOUNDS_KEY = "ENABLE_SOUNDS_KEY"
    private const val ALL_CAPS_KEY = "ALL_CAPS_KEY"

    private lateinit var _pref: SharedPreferences

    private fun getSetting(key: String, defValue: Boolean): Boolean {
        if (!::_pref.isInitialized) throw IllegalStateException("SharedPreferences not initialized")

        return _pref.getBoolean(key, defValue)
    }

    private fun setSetting(key: String, value: Boolean) {
        if (!::_pref.isInitialized) throw IllegalStateException("SharedPreferences not initialized")

        with(_pref.edit()) {
            putBoolean(key, value)
            apply()
        }
    }

    var isEnableSounds: Boolean
        get() {
            return getSetting(ENABLE_SOUNDS_KEY, defValue = true)
        }
        set(value) {
            setSetting(ENABLE_SOUNDS_KEY, value)
        }


    var isAllCaps: Boolean
        get() {
            return getSetting(ALL_CAPS_KEY, defValue = false)
        }
        set(value) {
            setSetting(ALL_CAPS_KEY, value)
        }

    fun initialize(sharedPref: SharedPreferences) {
        _pref = sharedPref
    }

}