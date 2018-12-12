package com.example.ghub.myapplication.bl

import java.util.*

data class GeorgianLetter(
        val order: Int,
        val mkhedruli: Char,
        val asomtavruli: Char,
        val nuskhuri: Char,
        val alternativeAsomtavruliSpelling: String,
        val latinEquivalent: String,
        val number: String,
        val name: String,
        val read: String,
        val learnOrder: Int,
        val words: List<String>) {

    private val _random = Random()

    val wordsShuffled get() = words.shuffled(_random)

}