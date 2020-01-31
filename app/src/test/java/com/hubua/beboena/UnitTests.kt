package com.hubua.beboena

import com.hubua.beboena.bl.GeorgianAlphabet
import com.hubua.beboena.bl.toKhucuri
import org.junit.Assert
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
        val strSentences1 = this.javaClass.classLoader.getResourceAsStream("assets/sentences1.txt")
        val strSentences2 = this.javaClass.classLoader.getResourceAsStream("assets/sentences2.txt")
        val strSentences3 = this.javaClass.classLoader.getResourceAsStream("assets/sentences3.txt")
        GeorgianAlphabet.initialize(strOga, strSentences1, strSentences2, strSentences3)
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
    fun sentences_shuffled() {

        val id10 = GeorgianAlphabet.lettersLearnOrdered.indexOfFirst {it.sentences.count() in 8..10}
        val id20 = GeorgianAlphabet.lettersLearnOrdered.indexOfFirst {it.sentences.count() in 15..20}

        GeorgianAlphabet.Cursor.setCurrentPosition(id10)
        val letter = GeorgianAlphabet.Cursor.currentLetter
        val try1Sentences = GeorgianAlphabet.Cursor.currentSentences
        val try2Sentences = GeorgianAlphabet.Cursor.currentSentences

        assertEquals(try1Sentences.count(), try2Sentences.count())

        println("${letter.letterKeySpelling} \t (${letter.sentences.count()})")

        var diffCount = 0
        try1Sentences.forEachIndexed { index, item ->
            val s1 = item
            val s2 = try2Sentences[index]
            if (s1 != s2) { diffCount++ }

            println("$s1 - $s2")
        }
        println("Different sentences: $diffCount")
        assertTrue(diffCount > 0)

    }

    @Test
    fun list_words() {

        val lettersList = GeorgianAlphabet.lettersLearnOrdered//.sortedBy { it.sentences.count() }

        println("Longest sentence:")
        val longestSentence = lettersList.flatMap { it.sentences }.maxBy { it.length }
        println(longestSentence)

        println("Loaded sentences count:")
        for (letter in lettersList) {
            println("${letter.letterKeySpelling} \t (${letter.sentences.count()})")
            for (sentence in letter.sentences.sortedBy { it.length }) {
                println("\t ${sentence}")
            }
        }

    }

}
