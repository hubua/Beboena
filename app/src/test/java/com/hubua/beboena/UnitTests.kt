package com.hubua.beboena

import com.hubua.beboena.bl.*
import org.junit.Before
import java.io.File
import java.io.InputStream
import kotlin.random.Random
import kotlin.test.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class UnitTests {

    @Before
    fun setup() {

        fun getAssetAsStream(name: String): InputStream {
            val projectDir = System.getProperty("user.dir")
            val file = File(projectDir, "src/main/assets/$name")
            return file.inputStream()
        }

        val strOga = getAssetAsStream("oga.tsv")
        val strResembles = getAssetAsStream("resembles.txt")
        val strSentences1 = getAssetAsStream("sentences1.txt")
        val strSentences2 = getAssetAsStream("sentences2.txt")

        GeorgianAlphabet.initialize(strOga, strResembles, strSentences1, strSentences2)
    }

    @Test
    fun `conversion is correct`() {

        assertEquals("Ⴀⴈ ⴈⴀ 123 n", "აი ია 123 n".toKhucuri())

        assertEquals("ႠႨ ႨႠ 123 n", "აი ია 123 n".toKhucuri(true))

        assertEquals("ჰოი ეი ა 123 n", "ჵ ჱ ა 123 n".toReadsAs())

        assertEquals("ა ბ გ დ", " ა ბ  გ   დ     ".toSpaceNormalized())
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

        assertEquals(1, lettersMap['ი']!!.sentences.count())
        assertEquals("აი ია", lettersMap['ი']!!.sentences[0])

        assertEquals(3, lettersMap['თ']!!.sentences.count())
    }

//    @Test
//    fun `sentences assigned to letters`() {
//
//        val sentencesTest =
//            "ა\n" +
//                    "(empty)\n" +
//                    "\n" +
//                    "გ\n" +
//                    "(spaces)\n" +
//                    "  დ\n" +
//                    "(tabs)\n" +
//                    "\t\tე\n" +
//                    "(repeat)\n" +
//                    "ა"
//
//        val strSentencesTest = ByteArrayInputStream(sentencesTest.toByteArray(Charsets.UTF_8))
//        val strOga = this.javaClass.classLoader!!.getResourceAsStream("assets/oga.tsv")
//        val strResembles = InputStream.nullInputStream()
//
//        GeorgianAlphabet.initialize(strOga, strResembles, strSentencesTest)
//
//        assertEquals(1, GeorgianAlphabet.lettersMap['ა']!!.sentences.count())
//        assertEquals(0, GeorgianAlphabet.lettersMap['ბ']!!.sentences.count())
//        assertEquals("დ", GeorgianAlphabet.lettersMap['დ']!!.sentences[0])
//        assertEquals("ე", GeorgianAlphabet.lettersMap['ე']!!.sentences[0])
//    }

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

        val statsChars: MutableMap<Char, Int> = mutableMapOf()

        val MAX_PAIRS = 5

        for ((index, value) in GeorgianAlphabet.lettersLearnOrdered.withIndex())
        {
            GeorgianAlphabet.Cursor.letterJumpTo(index)

            val letter = GeorgianAlphabet.Cursor.currentLetter
            val pairs = GeorgianAlphabet.Cursor.currentPairables

            val learnedResembles = letter.resembles.filter { GeorgianAlphabet.lettersMap[it]!!.learnOrder <= letter.learnOrder }

            if (letter.learnOrder > MAX_PAIRS) {
                assertEquals(MAX_PAIRS, pairs.count(), "Pairs count wrong")
                assertTrue(pairs.containsAll(learnedResembles))

                val unique = mutableSetOf<Char>()
                pairs.forEach {unique.add(it)}
                assertEquals(pairs.size, unique.size, "Unique chars do not match ${letter.letterModernSpelling} ${pairs}")
            }
            else {
                assertEquals(0, pairs.count())
            }

            println("${value.letterModernSpelling}" +
                    " -A:${String(value.resembles.toCharArray()).padEnd(4)}" +
                    " -L:${String(learnedResembles.toCharArray()).padEnd(4)}" +
                    " - ${String(pairs.toCharArray())}"
            )

            pairs.forEach { statsChars[it] = statsChars.getOrPut(it) { 0 } + 1 }
        }
        println(statsChars.toList().sortedByDescending { it.second }.toMap())
    }

    @Test
    fun debug_list_all_sentences() {

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
    fun debug_list_all_resembles() {

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
    fun debug_list_pairs() {

        val _random = Random

        val lettersLearnOrdered = GeorgianAlphabet.lettersLearnOrdered
        val lettersMap = GeorgianAlphabet.lettersMap

        val hist: MutableMap<Set<Char>, Int> = mutableMapOf()
        val statsCouples: MutableMap<Set<Char>, Int> = mutableMapOf(emptySet<Char>() to 0)
        val statsChars: MutableMap<Char, Int> = mutableMapOf()

        for (letter in lettersLearnOrdered) {

            val currentLetterLearnedResembles = listOf(letter.letterModernSpelling) + // current letter
                    letter.resembles.filter { lettersMap[it]!!.learnOrder < letter.learnOrder } // with all its resembles that are already learned

            val previousLettersLearnedResemblesCouples = lettersLearnOrdered
                .filter { it.learnOrder < letter.learnOrder} // previously learned letters
                .filter { it.resembles.isNotEmpty() } // which have resembles
                .associateWith { it.resembles.filter { resemble -> lettersMap[resemble]!!.learnOrder < letter.learnOrder } }  // with resembles that are already learned
                .filter { it.value.isNotEmpty() } // which have such resembles
                .map { sortedSetOf(it.key.letterModernSpelling, it.value.shuffled(_random).first()) } // take letter and one resemble
                .distinct()
                //.elementAtOrElse(0) {emptyList<Char>() }

            val oneCouple = if (previousLettersLearnedResemblesCouples.count() > 1) {
                previousLettersLearnedResemblesCouples.forEach { hist[it] = hist.getOrPut(it) { 0 } + 1 }
                val least = hist.filter { it.value == hist.minBy { minItem -> minItem.value }.value }.keys.shuffled().first()
                previousLettersLearnedResemblesCouples.forEach { hist[it] = hist[it]!! - 1  }
                hist[least] = hist[least]!! + 1
                least // return result
            } else emptySet()

            (previousLettersLearnedResemblesCouples).forEach { statsCouples.getOrPut(it) { 0 } }
            statsCouples[oneCouple] = statsCouples[oneCouple]!! + 1

            (currentLetterLearnedResembles + oneCouple).forEach { statsChars[it] = statsChars.getOrPut(it) { 0 } + 1 }

            println("${letter.letterModernSpelling} - ${currentLetterLearnedResembles.toString().padEnd(8)}\t: " +
                    "$oneCouple \t $previousLettersLearnedResemblesCouples >> $hist")
        }

        println(statsChars.toList().sortedByDescending { it.second }.toMap())
        println(statsCouples.toList().sortedByDescending { it.second }.toMap())
    }

    @Test
    fun debug_random_weighted(){

        val random = Random

        val sample = listOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h')
        val stats = sample.associateWith { 0 }.toMutableMap()


        repeat(100) {
            val n = sample.takeOneRandomWeighted(random)
            stats[n] = stats[n]!! + 1
        }

        println(stats)
    }

    @Test
    fun debug_random() {

        val COUNT = 1000
        var n = 0
        for (i in 0..COUNT) {
            val showAllCaps = (Random.nextInt(5) == 0) // Probability of 20%
            n += if (showAllCaps) 1 else 0

        }
        println("${n * 100f / COUNT } %")
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
