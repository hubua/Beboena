package com.ghub.beboena.bl

import android.content.SharedPreferences
import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*


object GeorgianAlphabet {

    private val _letters = mutableListOf<GeorgianLetter>()

    val lettersLearnOrdered get() = _letters.sortedBy { it.learnOrder }

    val lettersMap get() = _letters.associateBy({ it.letterKeySpelling }, { it })

    fun initialize(strOga: InputStream, strSentences: InputStream) {

        /**
         *  http://zetcode.com/articles/opencsv/
         */
        val isr = InputStreamReader(strOga)
        val tsvParser = CSVParserBuilder().withSeparator('\t').build()
        val tsvReader = CSVReaderBuilder(isr).withCSVParser(tsvParser).build()
        val ogaData = tsvReader.readAll().drop(n = 1)

        val sentencesData = strSentences.bufferedReader().readLines().distinct()

        /**
         * OGA.csv format
         * [0]Order [1]Modern [2]Asomtavruli [3]Nuskhuri [4]AlternativeAsomtavruliSpelling [5]LatinEquivalent
         * [6]NumberEquivalent [7]LetterName [8]ReadAs [9]LearnOrder [10]LearnOrder2 [11]Words
         */

        val orderedLetters = ogaData.associateBy({ it[1].toChar() }, { it[9].toInt() })
        // val orderedLetters2 = ogaData.map {it[1].toChar() to it[9].toInt()}.sortedBy { it.second }.toMap()

        val mapLetterSentences: MutableMap<Char, MutableList<String>> = mutableMapOf()
        orderedLetters.forEach { mapLetterSentences[it.key] = mutableListOf() }
        for (sentence in sentencesData) {
            var maxLetter = 0.toChar()
            for (currentLetter in sentence) {
                val currentLetterOrder = orderedLetters[currentLetter] ?: 0
                val maxLetterOrder = orderedLetters[maxLetter]
                maxLetter = if (currentLetterOrder > maxLetterOrder ?: 0) currentLetter else maxLetter
            }
            mapLetterSentences[maxLetter]!!.add(sentence)
        }

        _letters.clear()

        for (row in ogaData) {
            val letter = GeorgianLetter(
                order = row[0].toInt(),
                mkhedruli = row[1].toChar(),
                asomtavruli = row[2].toChar(),
                nuskhuri = row[3].toChar(),
                number = row[6],
                name = row[7],
                read = row[8],
                learnOrder = row[9].toInt(),
                sentences = mapLetterSentences[row[1].toChar()]!!.toList()
            )
            _letters.add(letter)
        }

    }

    object Cursor {

        private const val SAVED_POSITION_KEY = "SAVED_POSITION_KEY"

        private lateinit var _pref: SharedPreferences

        private var _currentPosition = 0

        private val _random = Random()

        val currentLetter get() = lettersLearnOrdered[_currentPosition]

        val currentSentencesShuffled get() = currentLetter.sentences.shuffled(_random) // Beware reshuffling on each call

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

        fun moveNext(): Int {
            setCurrentPosition(_currentPosition + 1)
            return _currentPosition
        }

        fun movePrev(): Int {
            setCurrentPosition(_currentPosition - 1)
            return _currentPosition
        }

    }

}

fun String.toKhucuri(withCapital: Boolean = false): String {

    val mkhedruli = this
    var khucuri = ""

    for ((index, letter) in mkhedruli.withIndex()) {
        if (GeorgianAlphabet.lettersMap.contains(letter)) {
            khucuri += if (withCapital && index == 0) GeorgianAlphabet.lettersMap[letter]?.asomtavruli else GeorgianAlphabet.lettersMap[letter]?.nuskhuri
        } else {
            khucuri += letter
        }
    }

    return khucuri
}

fun String.toChar(): Char {
    return this.toCharArray()[0]
}