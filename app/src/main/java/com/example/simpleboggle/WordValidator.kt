package com.example.simpleboggle

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

object WordValidator {
    private var wordList = mutableSetOf<String>() // Use a set for faster lookups

    suspend fun loadWords() = withContext(Dispatchers.IO) {
        println("Loading words...")
        try {
            val url = URL("https://raw.githubusercontent.com/dwyl/english-words/master/words.txt")
            val bufferedReader = BufferedReader(InputStreamReader(url.openStream()))
            bufferedReader.useLines { lines ->
                lines.forEach {
                    if (it.length >= 4) {
                        wordList.add(it.uppercase())
                    }
                }
            }
            println("Words loaded: ${wordList.size}")
        } catch (e: Exception) {
            println("Error loading words: ${e.message}")
            // Consider handling the error more gracefully, possibly with a retry mechanism or a fallback to a local word list.
        }
    }

    fun isValidWord(word: String): Boolean {
        // Check word length and vowel count first
        if (word.length < 4 || word.count { it in "AEIOUaeiou" } < 2) return false

        return word.uppercase() in wordList
    }

    fun getWordList(): Set<String> {
        return wordList
    }
}