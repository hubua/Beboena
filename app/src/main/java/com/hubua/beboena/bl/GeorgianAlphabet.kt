package com.hubua.beboena.bl

import android.content.SharedPreferences
import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.*


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

        private val _random = Random()

        private var _currentLetterPositionIndex = 0 // First letter at 0, unlike the Letter Order which starts with 1

        private var _currentSentencePositionIndex = 0 // First sentence at 0

        private var _currentSentences : List<String> = emptyList()

        private var _currentPairs : List<Char> = emptyList()

        val currentLetter get() = lettersLearnOrdered[_currentLetterPositionIndex]

        val currentSentence get() = _currentSentences[_currentSentencePositionIndex]

        val currentSentences get() =_currentSentences

        val currentSentencesProgress get() = _currentSentencePositionIndex + 1
        val currentSentencesCount get() =_currentSentences.count()

        val currentPairs get () = _currentPairs

        val hasPairs get() = _currentPairs.isNotEmpty()

        fun initialize(pref: SharedPreferences) {
            _pref = pref
            val savedPosition = _pref.getInt(SAVED_POSITION_KEY, 0)

            setCurrentLetterPosition(savedPosition, false)
        }

        fun getCurrentLetterPosition(): Int {
            return _currentLetterPositionIndex
        }

        private fun setCurrentLetterPosition(positionIndex: Int, updatePref: Boolean = true) {

            _currentLetterPositionIndex = if (positionIndex > 0 && positionIndex < lettersLearnOrdered.count()) positionIndex else 0

            _currentSentences = currentLetter.sentences.shuffled(_random).take(MAX_SENTENCES).sortedBy { it.length }
            _currentSentencePositionIndex = 0

            _currentPairs = if (currentLetter.learnOrder > MAX_PAIRS)
            {
                val learnedResembles = currentLetter.resembles
                    .filter { lettersMap[it]!!.learnOrder <= currentLetter.learnOrder }
                val learnedLetters = lettersLearnOrdered
                    .filter { it.learnOrder <= currentLetter.learnOrder}
                    .filter { !learnedResembles.contains(it.letterModernSpelling)  }
                    .shuffled(_random)
                    .take(MAX_PAIRS - learnedResembles.count())
                    .map { it.letterModernSpelling }
                //TODO include letters that were badly learned first

                (learnedResembles + learnedLetters).shuffled(_random) // return result
            }
            else
            {
                emptyList() // return result
            }

            if (updatePref) {
                if (::_pref.isInitialized) {
                    with(_pref.edit()) {
                        putInt(SAVED_POSITION_KEY, _currentLetterPositionIndex)
                        apply()
                    }
                }
            }
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

    for (letter in mkhedruliText) {
        if (GeorgianAlphabet.lettersMap.contains(letter)) {
            readAsText += GeorgianAlphabet.lettersMap[letter]?.letterReadsAs
        } else {
            readAsText += letter
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