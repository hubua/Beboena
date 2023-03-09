package com.hubua.beboena.bl

import android.content.SharedPreferences
import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import kotlin.random.Random
import kotlin.math.pow

object GeorgianAlphabet {

    private val _letters = mutableListOf<GeorgianLetter>()

    val lettersLearnOrdered get() = _letters.sortedBy { it.learnOrder }

    val lettersMap get() = _letters.associateBy({ it.letterModernSpelling }, { it })

    fun initialize(strOga: InputStream, strResembles: InputStream, vararg strSentences: InputStream) {

        val isr = InputStreamReader(strOga, StandardCharsets.UTF_8)
        val tsvParser = CSVParserBuilder().withSeparator('\t').build()
        val tsvReader = CSVReaderBuilder(isr).withCSVParser(tsvParser).build()
        val ogaList = tsvReader.readAll().drop(1)

        val resemblesList = strResembles.bufferedReader().use { it.readLines() }

        val sentencesList = strSentences.flatMap { it.bufferedReader().readLines() }
            .asSequence()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .filterNot { it.contains('(') }
            .distinct()
            .sorted()
            .toList()

        /**
         * OGA.csv format
         * [0]Order [1]Modern [2]Asomtavruli [3]Nuskhuri [4]LatinEquivalent [5]NumberEquivalent
         * [6]LetterName [7]ReadAs [8]LearnOrder [9]LearnOrder2
         */

        val ORDER_COL_ID = 0
        val MODERN_SPELLING_COL_ID_KEY = 1
        val LEARN_ORDER_COL_ID = 8

        val orderedLetters = ogaList.associateBy({ it[MODERN_SPELLING_COL_ID_KEY].toChar() }, { it[LEARN_ORDER_COL_ID].toInt() })

        val mapLetterSentences: MutableMap<Char, MutableList<String>> = mutableMapOf()
        orderedLetters.forEach { mapLetterSentences[it.key] = mutableListOf() }
        for (sentence in sentencesList) {
            var maxLetter = 0.toChar()
            for (currentLetter in sentence) {
                val currentLetterOrder = orderedLetters[currentLetter] ?: 0
                val maxLetterOrder = orderedLetters[maxLetter]
                maxLetter = if (currentLetterOrder > (maxLetterOrder ?: 0)) currentLetter else maxLetter
            }
            mapLetterSentences[maxLetter]!!.add(sentence)
        }

        _letters.clear()

        for (row in ogaList) {
            val mkhedruliKeyChar = row[MODERN_SPELLING_COL_ID_KEY].toChar()
            val letter = GeorgianLetter(
                order = row[ORDER_COL_ID].toInt(),
                mkhedruli = mkhedruliKeyChar,
                asomtavruli = row[2].toChar(),
                nuskhuri = row[3].toChar(),
                number = row[5].trim(),
                name = row[6],
                reads = row[7],
                learnOrder = row[LEARN_ORDER_COL_ID].toInt(),
                resembles = resemblesList
                    .filter { it.contains(mkhedruliKeyChar) }
                    .map { it.replace(mkhedruliKeyChar.toString(), "").single() }
                    .distinct()
                    .sorted(),
                sentences = mapLetterSentences[mkhedruliKeyChar]!!.toList()
            )
            _letters.add(letter)
        }

    }

    object Cursor {

        private const val MAX_SENTENCES = 10
        private const val MAX_PAIRS = 5

        private const val SAVED_POSITION_KEY = "SAVED_POSITION_KEY" // If renaming mind the backward compatibility with progress made by active users

        private lateinit var _pref: SharedPreferences

        private val _random = Random

        private var _currentLetterPositionIndex = 0 // First letter at 0, unlike the Letter Order which starts with 1

        private var _currentSentencePositionIndex = 0 // First sentence at 0

        private var _currentSentences: List<String> = emptyList()

        private var _currentPairables: List<Char> = emptyList()
        private var _allPairablesMap: MutableMap<Char, List<Char>> = mutableMapOf()

        val currentLetter get() = lettersLearnOrdered[_currentLetterPositionIndex]

        val currentSentence get() = _currentSentences[_currentSentencePositionIndex]
        val currentSentences get() =_currentSentences // Only used in UnitTests

        val currentSentencesProgress get() = _currentSentencePositionIndex + 1
        val currentSentencesCount get() =_currentSentences.count()

        val currentPairables get () = _currentPairables
        val hasPairs get() = _currentPairables.isNotEmpty()

        fun initialize(pref: SharedPreferences) {
            _pref = pref
            val savedPosition = _pref.getInt(SAVED_POSITION_KEY, 0)

            setCurrentLetterPosition(savedPosition, false)
        }

        fun letterJumpTo(position: Int): Int {
            setCurrentLetterPosition(position)
            return _currentLetterPositionIndex
        }

        fun letterTryAgain(): Int {
            setCurrentLetterPosition(_currentLetterPositionIndex)
            return _currentLetterPositionIndex
        }

        fun letterMoveNext(): Int {
            setCurrentLetterPosition(_currentLetterPositionIndex + 1)
            return _currentLetterPositionIndex
        }

        fun letterMovePrev(): Int {
            setCurrentLetterPosition(_currentLetterPositionIndex - 1)
            return _currentLetterPositionIndex
        }

        fun sentenceMoveNext(): Boolean {
            if (_currentSentencePositionIndex + 1 < _currentSentences.count()) {
                _currentSentencePositionIndex++
                return true
            } else {
                return false
            }
        }

        fun getCurrentLetterPosition(): Int {
            return _currentLetterPositionIndex
        }

        private fun setCurrentLetterPosition(positionIndex: Int, updatePref: Boolean = true) {

            _currentLetterPositionIndex = if (positionIndex > 0 && positionIndex < lettersLearnOrdered.count()) positionIndex else 0

            _currentSentences = currentLetter.sentences.shuffled(_random).take(MAX_SENTENCES).sortedBy { it.length }
            _currentSentencePositionIndex = 0

            buildPairablesMap()
            _currentPairables = if (currentLetter.learnOrder > MAX_PAIRS) _allPairablesMap[currentLetter.letterModernSpelling]!! else emptyList()

            if (updatePref) {
                if (::_pref.isInitialized) {
                    with(_pref.edit()) {
                        putInt(SAVED_POSITION_KEY, _currentLetterPositionIndex)
                        apply()
                    }
                }
            }
        }

        private fun buildPairablesMap() {

            /*
            * Resembles - the letters that looks alike
            * Couples - a letter and one of its resembles
            * Pairs - old and modern letters counterparts
            */

            val hist: MutableMap<Set<Char>, Int> = mutableMapOf()

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

                val oneCouple = if (previousLettersLearnedResemblesCouples.count() > 1) {
                    previousLettersLearnedResemblesCouples.forEach { hist[it] = hist.getOrPut(it) { 0 } + 1 }
                    val least = hist.filter { it.value == hist.minBy { minItem -> minItem.value }.value }.keys.shuffled().first()
                    previousLettersLearnedResemblesCouples.forEach { hist[it] = hist[it]!! - 1  }
                    hist[least] = hist[least]!! + 1
                    least // return result
                } else emptySet()

                val otherLearnedLetters = lettersLearnOrdered
                    .filter { it.learnOrder < currentLetter.learnOrder}
                    .filter { !(currentLetterLearnedResembles + oneCouple).contains(it.letterModernSpelling)  }
                    .map { it.letterModernSpelling }
                    .shuffled(_random)

                val pairables = ((currentLetterLearnedResembles + oneCouple).toSet().toList() + otherLearnedLetters)
                    .take(MAX_PAIRS)
                    .shuffled(_random)

                _allPairablesMap[letter.letterModernSpelling] = pairables

            }

        }


    }

}

fun String.toKhucuri(isAllCaps: Boolean = false): String {

    val mkhedruliText = this
    var khucuriText = ""

    for ((index, letter) in mkhedruliText.withIndex()) {
        if (GeorgianAlphabet.lettersMap.contains(letter)) {
            khucuriText += if (isAllCaps || index == 0) GeorgianAlphabet.lettersMap[letter]?.asomtavruli else GeorgianAlphabet.lettersMap[letter]?.nuskhuri
        } else {
            khucuriText += letter
        }
    }

    return khucuriText
}

fun String.toReadsAs(): String {

    val mkhedruliText = this
    var readAsText = ""

    for (mkhedruliChar in mkhedruliText) {
        if (GeorgianAlphabet.lettersMap.contains(mkhedruliChar)) {
            readAsText += GeorgianAlphabet.lettersMap[mkhedruliChar]?.letterReadsAs
        } else {
            readAsText += mkhedruliChar
        }
    }

    return readAsText
}

fun String.toSpaceNormalized(): String {

    val originalText = this

    val normalizedText = originalText
        .trim()
        .replace("\\s+".toRegex(), " ")

    return normalizedText
}

fun String.toChar(): Char {
    return this.toCharArray()[0]
}

fun <T> List<T>.takeOneRandomWeighted(random: Random): T {

    if (this.isEmpty())
        throw java.lang.IllegalArgumentException("Collection is empty")

    val C = 1.2F

    val w = (0..this.size).withIndex().associate { it.index to (C.pow(it.value) - 1) }
    val r = w.values.max() * random.nextFloat()
    val i = w.toList().last { r >= it.second}.first

    return this[i]
}
