package com.example.ghub.beboena

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.example.ghub.beboena.bl.GeorgianABC

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import org.junit.BeforeClass

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {



    companion object {

        private lateinit var appContext : Context

        @JvmStatic
        @BeforeClass
        fun setup() {
            appContext = InstrumentationRegistry.getTargetContext()
        }
    }

    @Test
    fun useAppContext() {
        // Context of the app under test.
        assertEquals("com.example.ghub.beboena", appContext.packageName)

    }

    @Test
    fun file_read() {

        val strOga = appContext.assets.open("oga.tsv")
        val strSentences = appContext.assets.open("sentences.txt")
        GeorgianABC.initialize(strOga, strSentences);

        val letters = GeorgianABC.lettersToLearn

        assertEquals(33 + 5, letters.count())
    }



}
