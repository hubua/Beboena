package com.ghub.beboena.bl

import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import java.io.InputStream
import java.io.InputStreamReader


object GeorgianAlphabet {

    private val _letters = mutableListOf<GeorgianLetter>()

    val lettersByOrderIndex get() = _letters.sortedBy{ it.learnOrder }

    val lettersById get() = _letters.associateBy({ it.mkhedruli}, {it})

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

        val orderedLetters = ogaData.associateBy({it[1].toChar()}, {it[9].toInt()})
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
                    alternativeAsomtavruliSpelling = row[4],
                    latinEquivalent = row[5],
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

        private var _currentLetter = GeorgianAlphabet.lettersByOrderIndex[0]

        val currentLetter get() = _currentLetter

        fun setCurrentPosition(pos: Int): Boolean {
            _currentLetter = GeorgianAlphabet.lettersByOrderIndex[pos]
            return true
        }

        fun getCurrentPosition(): Int {
            return _currentLetter.learnOrder
        }

        fun nextLetter(): GeorgianLetter {
            var pos = _currentLetter.learnOrder
            pos++
            _currentLetter = GeorgianAlphabet.lettersByOrderIndex[pos]
            return _currentLetter
        }

        fun prevLetter(): GeorgianLetter {
            var pos = _currentLetter.learnOrder
            pos--
            _currentLetter = GeorgianAlphabet.lettersByOrderIndex[pos]
            return _currentLetter
        }

    }

}

fun String.toKhucuri(withCapital: Boolean = false): String {

    val mkhedruli = this
    var khucuri = ""

    for ((index, letter) in mkhedruli.withIndex()) {
        if (GeorgianAlphabet.lettersById.contains(letter)) {
            khucuri += if (withCapital && index == 0) GeorgianAlphabet.lettersById[letter]?.asomtavruli else GeorgianAlphabet.lettersById[letter]?.nuskhuri
        }
        else {
            khucuri += letter
        }
    }

    return khucuri
}

fun String.toChar(): Char {
    return this.toCharArray()[0]
}