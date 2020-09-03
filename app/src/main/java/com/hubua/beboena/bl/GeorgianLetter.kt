package com.hubua.beboena.bl

data class GeorgianLetter(
    val order: Int,
    val mkhedruli: Char,    // Modern
    val asomtavruli: Char,  // Old caps
    val nuskhuri: Char,     // Old
    val number: String,
    val name: String,
    val read: String,
    val learnOrder: Int,
    val sentences: List<String>) {

    val letterKeySpelling get() = mkhedruli

    val hasSentences get() = (learnOrder > 1) && (learnOrder < 100)

}