package com.hubua.beboena.bl

import android.content.SharedPreferences
import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*


object GeorgianAlphabet {

    private val _letters = mutableListOf<GeorgianLetter>()

    val lettersLearnOrdered get() = _letters.sortedBy { it.learnOrder }

    val lettersMap get() = _letters.associateBy({ it.letterModernSpelling }, { it })

    fun initialize(strOga: InputStream, strResembles: InputStream, vararg strSentences: InputStream) {

        val isr = InputStreamReader(strOga)
        val tsvParser = CSVParserBuilder().withSeparator('\t').build()
        val tsvReader = CSVReaderBuilder(isr).withCSVParser(tsvParser).build()
        val ogaList = tsvReader.readAll().drop(n = 1)

        val resemblesList = strResembles.bufferedReader().use { it.readText() }.split(' ')

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
         * [0]Order [1]Modern [2]Asomtavruli [3]Nuskhuri [4]AlternativeAsomtavruliSpelling [5]LatinEquivalent
         * [6]NumberEquivalent [7]LetterName [8]ReadAs [9]LearnOrder [10]LearnOrder2 [11]Words
         */
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
                maxLetter = if (currentLetterOrder > maxLetterOrder ?: 0) currentLetter else maxLetter
            }
            mapLetterSentences[maxLetter]!!.add(sentence)
        }

        _letters.clear()

        for (row in ogaList) {
            val letter = GeorgianLetter(
                order = row[0].toInt(),
                mkhedruli = row[MODERN_SPELLING_COL_ID].toChar(),
                asomtavruli = row[2].toChar(),
                nuskhuri = row[3].toChar(),
                number = row[5].trim(),
                name = row[6],
                reads = row[7],
                learnOrder = row[LEARN_ORDER_COL_ID].toInt(),
                resembles = resemblesList.filter { it.contains(row[1].toChar()) },
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

        private var _currentPosition = 0

        private val _random = Random()

        val currentLetter get() = lettersLearnOrdered[_currentPosition]

        val currentSentences get() = currentLetter.sentences.shuffled(_random).take(MAX_SENTENCES).sortedBy { it.length } // Beware reshuffling on each call

        val currentResembles get () = currentLetter.resembles.shuffled(_random).take(MAX_RESEMBLES)

        fun initialize(sharedPref: SharedPreferences) {
            _pref = sharedPref
            val savedPosition = _pref.getInt(SAVED_POSITION_KEY, 0)
            _currentPosition = savedPosition
        }

        fun getCurrentPosition(): Int {
            return _currentPosition
        }

        fun setCurrentPosition(position: Int): Int {

            _currentPosition = if (position > 0 && position < lettersLearnOrdered.count()) position else 0

            if (::_pref.isInitialized) {
                with(_pref.edit()) {
                    putInt(SAVED_POSITION_KEY, _currentPosition)
                    apply()
                }
            }

            return _currentPosition
        }

        fun positionTryAgain(): Int {
            return _currentPosition
        }

        fun positionMoveNext(): Int {
            setCurrentPosition(_currentPosition + 1)
            return _currentPosition
        }

        fun positionMovePrev(): Int {
            setCurrentPosition(_currentPosition - 1)
            return _currentPosition
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

fun String.toChar(): Char {
    return this.toCharArray()[0]
}