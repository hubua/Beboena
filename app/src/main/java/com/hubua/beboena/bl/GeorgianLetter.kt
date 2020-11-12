package com.hubua.beboena.bl

data class GeorgianLetter(
    val order: Int,
    val mkhedruli: Char,    // Modern
    val asomtavruli: Char,  // Old Caps
    val nuskhuri: Char,     // Old
    val number: String,
    val name: String,
    val reads: String,
    val learnOrder: Int,
    val resembles: List<String>,
    val sentences: List<String>) {

    val letterModernSpelling get() = mkhedruli
    val letterReadsAs get() = if (reads.isEmpty()) mkhedruli.toString() else reads
    val letterReadsAsSpells get() = letterModernSpelling.toString() == letterReadsAs

    val hasSentences get() = (learnOrder > 1) && (sentences.count() > 0)

}