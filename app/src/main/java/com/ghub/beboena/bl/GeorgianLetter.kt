package com.ghub.beboena.bl

import java.util.*

data class GeorgianLetter(
        val order: Int,
        val mkhedruli: Char,
        val asomtavruli: Char,
        val nuskhuri: Char,
        val number: String,
        val name: String,
        val read: String,
        val learnOrder: Int,
        val sentences: List<String>) {

    val letterKeySpelling get() = mkhedruli

    val hasSentences get() = learnOrder > 1 && learnOrder < 100

}