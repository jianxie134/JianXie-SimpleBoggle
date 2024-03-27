package com.example.simpleboggle

object WordValidator {
    private val words = mutableListOf<String>()

    fun loadWords() {
        // Load the word list from a file or a server
    }

    fun isValidWord(word: String, wordList: Set<String>): Boolean {
        // Check word length and vowel count first
        if (word.length < 4 || word.count { it in "AEIOUaeiou" } < 2) return false

        return word.uppercase() in wordList
    }
}