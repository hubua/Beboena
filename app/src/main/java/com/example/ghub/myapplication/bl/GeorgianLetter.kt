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
        val sentences: List<String>) {

    private val _random = Random()

    val sentencesShuffled get() = sentences.shuffled(_random)

}