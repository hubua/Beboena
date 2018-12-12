package com.example.ghub.myapplication

import android.content.res.Resources
import com.example.ghub.myapplication.bl.GeorgianABC
import com.example.ghub.myapplication.bl.toKhucuri
import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import org.apache.commons.lang3.ClassUtils.getPackageName
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.BeforeClass
import java.io.InputStreamReader

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    /*
    https://medium.com/@yair.kukielka/android-unit-tests-explained-219b04dc55b5
    https://medium.com/@yair.kukielka/android-unit-tests-explained-part-2-a0f1e1413569
    */


    @Before
    fun setup() {
        val strOga = this.javaClass.classLoader.getResourceAsStream("assets/oga.tsv")
        val strSentences = this.javaClass.classLoader.getResourceAsStream("assets/sentences.txt")
        GeorgianABC.initialize(strOga, strSentences);
    }

    @Test
    fun initialize_isCorrect() {

        val letters = GeorgianABC.lettersToLearn

        assertEquals(33 + 5, letters.count())
    }

    @Test
    fun translation_isCorrect() {

        assertEquals("ⴑⴀⴊⴀⴋⴈ 123 n", "სალამი 123 n".toKhucuri())

        assertEquals("Ⴑⴀⴊⴀⴋⴈ 123 n", "სალამი 123 n".toKhucuri(true))
    }

    @Test
    fun words_loaded() {

        val letters = GeorgianABC.lettersToLearn

        assertEquals(0, letters['ა']!!.words.count())

        assertEquals("აი ია", letters['ი']!!.words[0])

        assertNotEquals(letters['ო']!!.words[0], letters['ო']!!.wordsShuffled[0])
    }

}
