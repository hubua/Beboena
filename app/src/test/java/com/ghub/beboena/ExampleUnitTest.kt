package com.ghub.beboena

import com.ghub.beboena.bl.GeorgianAlphabet
import com.ghub.beboena.bl.toKhucuri
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Before
    fun setup() {
        val strOga = this.javaClass.classLoader.getResourceAsStream("assets/oga.tsv")
        val strSentences = this.javaClass.classLoader.getResourceAsStream("assets/sentences.txt")
        GeorgianAlphabet.initialize(strOga, strSentences)
    }

    @Test
    fun initialize_isCorrect() {

        val letters = GeorgianAlphabet.lettersByOrder

        assertEquals(33 + 5, letters.count())
    }

    @Test
    fun translation_isCorrect() {

        assertEquals("ⴑⴀⴊⴀⴋⴈ 123 n", "სალამი 123 n".toKhucuri())

        assertEquals("Ⴑⴀⴊⴀⴋⴈ 123 n", "სალამი 123 n".toKhucuri(true))
    }

    @Test
    fun words_loaded() {

        val lettersMap = GeorgianAlphabet.lettersById

        assertEquals(0, lettersMap['ა']!!.sentences.count())

        assertEquals("აი ია", lettersMap['ი']!!.sentences[0])

        assertNotEquals(lettersMap['ო']!!.sentences[0], lettersMap['ო']!!.sentencesShuffled[0]) //TODO flickering test because of random
    }

}
