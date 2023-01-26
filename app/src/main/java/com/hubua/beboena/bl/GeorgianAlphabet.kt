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

        val resemblesList = strResembles.bufferedReader().use { it.readText() }
            .split(' ')

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
        val MODERN_SPELLING_COL_ID = 1
        val LEARN_ORDER_COL_ID = 8

        val orderedLetters = ogaList.associateBy({ it[MODERN_SPELLING_COL_ID].toChar() }, { it[LEARN_ORDER_COL_ID].toInt() })

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
            val letter = GeorgianLetter(
                order = row[ORDER_COL_ID].toInt(),
                mkhedruli = row[MODERN_SPELLING_COL_ID].toChar(),
                asomtavruli = row[2].toChar(),
                nuskhuri = row[3].toChar(),
                number = row[5].trim(),
                name = row[6],
                reads = row[7],
                learnOrder = row[LEARN_ORDER_COL_ID].toInt(),
                resembles = resemblesList.filter { it.contains(row[MODERN_SPELLING_COL_ID].toChar()) },
                sentences = mapLetterSentences[row[MODERN_SPELLING_COL_ID].toChar()]!!.toList()
            )
            _letters.add(letter)
        }

    }

    object Cursor {

        private const val MAX_SENTENCES = 10
        private const val MAX_RESEMBLES = 3

        private const val SAVED_POSITION_KEY = "SAVED_POSITION_KEY" // If renaming mind the backward compatibility with progress made by active users

        private lateinit var _pref: SharedPreferences

        private val _random = Random()

        private var _currentLetterPosition = 0

        private var _currentSentencePosition = 0

        private var _currentSentences : List<String> = emptyList()

        val currentLetter get() = lettersLearnOrdered[_currentLetterPosition]

        val currentSentence get() = _currentSentences[_currentSentencePosition]

        val currentSentences get() =_currentSentences

        val currentSentencesProgress get() = _currentSentencePosition + 1
        val currentSentencesCount get() =_currentSentences.count()

        val currentResembles get () = currentLetter.resembles.shuffled(_random).take(MAX_RESEMBLES)

        fun initialize(pref: SharedPreferences) {
            _pref = pref
            val savedPosition = _pref.getInt(SAVED_POSITION_KEY, 0)

            setCurrentLetterPosition(savedPosition, false)
        }

        fun getCurrentLetterPosition(): Int {
            return _currentLetterPosition
        }

        private fun setCurrentLetterPosition(position: Int, updatePref: Boolean = true) {

            _currentLetterPosition = if (position > 0 && position < lettersLearnOrdered.count()) position else 0

            _currentSentences = currentLetter.sentences.shuffled(_random).take(MAX_SENTENCES).sortedBy { it.length }
            _currentSentencePosition = 0

            if (updatePref) {
                if (::_pref.isInitialized) {
                    with(_pref.edit()) {
                        putInt(SAVED_POSITION_KEY, _currentLetterPosition)
                        apply()
                    }
                }
            }
        }

        fun letterJumpTo(position: Int): Int {
            setCurrentLetterPosition(position)
            return _currentLetterPosition
        }

        fun letterTryAgain(): Int {
            setCurrentLetterPosition(_currentLetterPosition)
            return _currentLetterPosition
        }

        fun letterMoveNext(): Int {
            setCurrentLetterPosition(_currentLetterPosition + 1)
            return _currentLetterPosition
        }

        fun letterMovePrev(): Int {
            setCurrentLetterPosition(_currentLetterPosition - 1)
            return _currentLetterPosition
        }

        fun sentenceMoveNext(): Boolean {
            if (_currentSentencePosition + 1 < _currentSentences.count()) {
                _currentSentencePosition++
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