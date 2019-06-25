package com.example.ghub.myapplication

import com.example.ghub.myapplication.bl.GeorgianABC
import com.example.ghub.myapplication.bl.toKhucuri
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

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
        GeorgianABC.initialize(strOga, strSentences)
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

        assertEquals(0, letters['ა']!!.sentences.count())

        assertEquals("აი ია", letters['ი']!!.sentences[0])

        assertNotEquals(letters['ო']!!.sentences[0], letters['ო']!!.sentencesShuffled[0])
    }

}
