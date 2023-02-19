package com.hubua.beboena

import android.content.Context
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.hubua.beboena.bl.GeorgianAlphabet

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.BeforeClass

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class InstrumentedTests {

    companion object {

        private lateinit var appContext: Context

        @JvmStatic
        @BeforeClass
        fun setup() {
            appContext = InstrumentationRegistry.getTargetContext()
        }
    }

    @Test
    fun useAppContext() {
        // Context of the app under test.
        assertEquals("com.hubua.beboena", appContext.packageName)

    }

    @Test
    fun file_read() {

        val strOga = appContext.assets.open("oga.tsv")
        val strResembles = appContext.assets.open("resembles.txt")
        val strSentences = appContext.assets.open("sentences.txt")
        GeorgianAlphabet.initialize(strOga, strResembles, strSentences);

        val letters = GeorgianAlphabet.lettersLearnOrdered

        assertEquals(33 + 5, letters.count())
    }

}
