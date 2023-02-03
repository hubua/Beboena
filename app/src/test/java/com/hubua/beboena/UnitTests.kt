package com.hubua.beboena

import com.hubua.beboena.bl.GeorgianAlphabet
import com.hubua.beboena.bl.toKhucuri
import com.hubua.beboena.bl.toReadsAs
import com.hubua.beboena.bl.toSpaceNormalized
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import java.io.ByteArrayInputStream
import java.io.InputStream

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class UnitTests {

    @Before
    fun setup() {
        val strOga = this.javaClass.classLoader!!.getResourceAsStream("assets/oga.tsv")
        val strResembles = this.javaClass.classLoader!!.getResourceAsStream("assets/resembles.txt")
        val strSentences1 = this.javaClass.classLoader!!.getResourceAsStream("assets/sentences1.txt")
        val strSentences2 = this.javaClass.classLoader!!.getResourceAsStream("assets/sentences2.txt")
        GeorgianAlphabet.initialize(strOga, strResembles, strSentences1, strSentences2)
    }

    @Test
    fun `sentences assigned to letters`() {

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
        val strOga = this.javaClass.classLoader!!.getResourceAsStream("assets/oga.tsv")
        val strResembles = InputStream.nullInputStream()

        GeorgianAlphabet.initialize(strOga, strResembles, strSentencesTest)

        assertEquals(1, GeorgianAlphabet.lettersMap['ა']!!.sentences.count())
        assertEquals(0, GeorgianAlphabet.lettersMap['ბ']!!.sentences.count())
        assertEquals("დ", GeorgianAlphabet.lettersMap['დ']!!.sentences[0])
        assertEquals("ე", GeorgianAlphabet.lettersMap['ე']!!.sentences[0])
    }

    @Test
    fun `letters map loaded`() {

        val lettersMap = GeorgianAlphabet.lettersMap

        assertEquals(33 + 5, lettersMap.count())

        assertEquals(1, lettersMap['ა']!!.order)
        assertEquals("1", lettersMap['ა']!!.number)

        assertEquals(12, lettersMap['ლ']!!.order)
        assertEquals("30", lettersMap['ლ']!!.number)
    }

    @Test
    fun `sentences loaded`() {

        val lettersMap = GeorgianAlphabet.lettersMap

        assertEquals(0, lettersMap['ა']!!.sentences.count())

        assertEquals("აი ია", lettersMap['ი']!!.sentences[0])
    }

    @Test
    fun `sentences shuffled`() {

        val posOf10 = GeorgianAlphabet.lettersLearnOrdered.indexOfFirst { it.sentences.count() in 8..10 }
        //val id20 = GeorgianAlphabet.lettersLearnOrdered.indexOfFirst { it.sentences.count() in 15..20 }

        GeorgianAlphabet.Cursor.letterJumpTo(posOf10)
        val letter = GeorgianAlphabet.Cursor.currentLetter
        val try1Sentences = GeorgianAlphabet.Cursor.currentSentences
        GeorgianAlphabet.Cursor.letterTryAgain()
        val try2Sentences = GeorgianAlphabet.Cursor.currentSentences

        assertEquals(try1Sentences.count(), try2Sentences.count())

        println("${letter.letterModernSpelling} \t (${letter.sentences.count()})")

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
    fun `pairs assigned to letters`() {

        val MAX_PAIRS = 5

        for ((index, value) in GeorgianAlphabet.lettersLearnOrdered.withIndex())
        {
            GeorgianAlphabet.Cursor.letterJumpTo(index)

            val letter = GeorgianAlphabet.Cursor.currentLetter
            val pairs = GeorgianAlphabet.Cursor.currentPairs

            val learnedResembles = letter.resembles.filter { GeorgianAlphabet.lettersMap[it]!!.learnOrder <= letter.learnOrder }

            if (letter.learnOrder > MAX_PAIRS) {
                assertEquals(MAX_PAIRS, pairs.count())
                assertTrue(pairs.containsAll(learnedResembles))
            }
            else {
                assertEquals(0, pairs.count())
            }

            println("${value.letterModernSpelling} " +
                    "- ${String(pairs.sortedBy { GeorgianAlphabet.lettersMap[it]!!.learnOrder }.toCharArray())} " +
                    "- ${String(value.resembles.toCharArray())} " +
                    "- ${String(learnedResembles.toCharArray())}")

        }
    }

    @Test
    fun `conversion is correct`() {

        assertEquals("Ⴀⴈ ⴈⴀ 123 n", "აი ია 123 n".toKhucuri())

        assertEquals("ႠႨ ႨႠ 123 n", "აი ია 123 n".toKhucuri(true))

        assertEquals("ჰოი ეი ა 123 n", "ჵ ჱ ა 123 n".toReadsAs())

        assertEquals("ა ბ გ დ", " ა ბ  გ   დ     ".toSpaceNormalized())
    }

    @Test
    fun debug_list_sentences() {

        val lettersList = GeorgianAlphabet.lettersLearnOrdered

        println("Longest sentence:")
        val longestSentence = lettersList.flatMap { it.sentences }.maxByOrNull { it.length }
        println(longestSentence)

        println("Loaded sentences count:")
        for (letter in lettersList) {
            println("${letter.letterModernSpelling} '${letter.letterReadsAs}' (${letter.sentences.count()})")
            for (sentence in letter.sentences.sortedBy { it.length }) {
                println("\t${sentence}")
            }
        }

    }

    @Test
    fun debug_list_resembles() {

        val lettersList = GeorgianAlphabet.lettersLearnOrdered
        val lettersMap = GeorgianAlphabet.lettersMap

        for (letter in lettersList) {
            if (letter.resembles.count() > 0) {

                println("${letter.letterModernSpelling} (${letter.resembles.count()})")

                for (resemble in letter.resembles) {
                    print("  ${letter.mkhedruli} ${resemble} ")
                    print(" - ")
                    println("${letter.nuskhuri} ${lettersMap[resemble]!!.nuskhuri} ")
                }
            }
        }

    }

    @Test
    fun debug_random() {

        var n = 0
        for (i in 0..1000) {
            val showAllCaps = (kotlin.random.Random.nextInt(5) == 0) // Probability of 20%
            if (showAllCaps) { n++ }

        }
        println("${n / 1000.0 * 100} %")
    }

    /*
    @Test
    fun letters_similarity_matrix() {

        val lettersList = GeorgianAlphabet.lettersLearnOrdered

        for (letterX in lettersList) {
            print("${letterX.mkhedruli}\t")
            for (letterY in lettersList) {
                if (letterX != letterY) {
                    print("${letterX.nuskhuri}${letterY.nuskhuri}\t")
                }
            }
            println()
        }

        for (letterX in lettersList) {
            print("${letterX.mkhedruli}\t")
            for (letterY in lettersList) {
                if (letterX != letterY) {
                    print("${letterX.asomtavruli}${letterY.asomtavruli}\t")
                }
            }
            println()
        }

    }
    */

}
