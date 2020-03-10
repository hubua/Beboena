package com.hubua.beboena

import com.hubua.beboena.bl.GeorgianAlphabet
import com.hubua.beboena.bl.toKhucuri
import org.junit.Assert
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import java.io.ByteArrayInputStream

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
        //val strSentences2 = this.javaClass.classLoader.getResourceAsStream("assets/sentences2.txt")
        GeorgianAlphabet.initialize(strOga, strSentences1)
    }

    @Test
    fun filter_isCorrect() {

        val sentencesTest =
            "ა\n" +
            "(empty)\n" +
            "\n" +
            "გ\n" +
            "(spaces)\n" +
            "  დ\n" +
            "(tabs)\n" +
            "\t\tე\n" +
            "(repeat)\n" +
            "ა"
        val strSentencesTest = ByteArrayInputStream(sentencesTest.toByteArray(Charsets.UTF_8))

        val strOga = this.javaClass.classLoader.getResourceAsStream("assets/oga.tsv")

        GeorgianAlphabet.initialize(strOga, strSentencesTest)

        assertEquals(1, GeorgianAlphabet.lettersMap['ა']!!.sentences.count())
        assertEquals(0, GeorgianAlphabet.lettersMap['ბ']!!.sentences.count())
        assertEquals("დ", GeorgianAlphabet.lettersMap['დ']!!.sentences[0])
        assertEquals("ე", GeorgianAlphabet.lettersMap['ე']!!.sentences[0])
    }

    @Test
    fun initialize_isCorrect() {

        val letters = GeorgianAlphabet.lettersLearnOrdered

        assertEquals(33 + 5, letters.count())
    }

    @Test
    fun translation_isCorrect() {

        assertEquals("Ⴀⴈ ⴈⴀ 123 n", "აი ია 123 n".toKhucuri())

        assertEquals("ႠႨ ႨႠ 123 n", "აი ია 123 n".toKhucuri(true))
    }

    @Test
    fun words_loaded() {

        val lettersMap = GeorgianAlphabet.lettersMap

        assertEquals(0, lettersMap['ა']!!.sentences.count())

        assertEquals("აი ია", lettersMap['ი']!!.sentences[0])
    }

    @Test
    fun sentences_shuffled() {

        val id10 = GeorgianAlphabet.lettersLearnOrdered.indexOfFirst { it.sentences.count() in 8..10 }
        val id20 = GeorgianAlphabet.lettersLearnOrdered.indexOfFirst { it.sentences.count() in 15..20 }

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
            if (s1 != s2) {
                diffCount++
            }

            println("$s1 - $s2")
        }
        println("Different sentences: $diffCount")
        assertTrue(diffCount > 0)

    }

    @Test
    fun list_words() {

        val lettersList = GeorgianAlphabet.lettersLearnOrdered

        println("Longest sentence:")
        val longestSentence = lettersList.flatMap { it.sentences }.maxBy { it.length }
        println(longestSentence)

        println("Loaded sentences count:")
        for (letter in lettersList) {
            println("${letter.letterKeySpelling} (${letter.sentences.count()})")
            for (sentence in letter.sentences.sortedBy { it.length }) {
                println("\t${sentence}")
            }
        }

    }

}
