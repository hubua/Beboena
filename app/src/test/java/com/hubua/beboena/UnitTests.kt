package com.hubua.beboena

import com.hubua.beboena.bl.GeorgianAlphabet
import com.hubua.beboena.bl.toKhucuri
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class UnitTests {

    @Before
    fun setup() {
        val strOga = this.javaClass.classLoader.getResourceAsStream("assets/oga.tsv")
        val strSentences = this.javaClass.classLoader.getResourceAsStream("assets/sentences.txt")
        GeorgianAlphabet.initialize(strOga, strSentences)
    }

    @Test
    fun initialize_isCorrect() {

        val letters = GeorgianAlphabet.lettersLearnOrdered

        assertEquals(33 + 5, letters.count())
    }

    @Test
    fun translation_isCorrect() {

        assertEquals("ⴑⴀⴊⴀⴋⴈ 123 n", "სალამი 123 n".toKhucuri())

        assertEquals("Ⴑⴀⴊⴀⴋⴈ 123 n", "სალამი 123 n".toKhucuri(true))
    }

    @Test
    fun words_loaded() {

        val lettersMap = GeorgianAlphabet.lettersMap

        assertEquals(0, lettersMap['ა']!!.sentences.count())

        assertEquals("აი ია", lettersMap['ი']!!.sentences[0])
    }

    @Test
    fun list_words() {

        val lettersList = GeorgianAlphabet.lettersLearnOrdered//.sortedBy { it.sentences.count() }

        println("Longest sentence:");
        val longestSentence = lettersList.flatMap { it.sentences }.maxBy { it.length }
        println(longestSentence)

        println("Loaded sentences count:");
        for (letter in lettersList) {
            println("${letter.letterKeySpelling} \t (${letter.sentences.count()})")
            for (sentence in letter.sentences.sortedBy { it.length }) {
                println("\t ${sentence}")
            }
        }

    }

}
