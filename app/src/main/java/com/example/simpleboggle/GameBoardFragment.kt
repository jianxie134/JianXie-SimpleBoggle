package com.example.simpleboggle

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import kotlin.math.abs

class GameBoardFragment : Fragment() {
    private lateinit var gridLayout: GridLayout
    private var selectedLetters = StringBuilder()
    private var actionsListener: GameActionsListener? = null
    private var selectedButtons = mutableListOf<Button>()
    private lateinit var selectedLettersTextView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_game_board, container, false)
        gridLayout = view.findViewById(R.id.gameBoardGrid)
        initializeBoard()

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is GameActionsListener) {
            actionsListener = context
        } else {
            throw RuntimeException("$context must implement GameActionsListener")
        }
    }

    fun initializeBoard() {
        gridLayout.removeAllViews() // Clear the gridLayout
        val letters = generateRandomLetters()
        for (i in 0 until 16) { // For a 4x4 grid
            val button = Button(context).apply {
                text = letters[i].toString()
                setOnClickListener {
                    // Handle letter selection
                    onLetterSelected(this)
                }
            }
            gridLayout.addView(button)
        }

        // Add the selectedLettersTextView TextView back to the gridLayout
        selectedLettersTextView = TextView(context).apply {
            id = R.id.selectedLettersTextView
            textSize = 18f
            text = selectedLetters.toString()
        }
        gridLayout.addView(selectedLettersTextView)
    }

    private fun generateRandomLetters(): List<Char> {
        // This is a simplified version. Customize this method based on your game's rules.
        val vowels = "AEIOU".toList()
        val consonants = ('A'..'Z').filter { !vowels.contains(it) }
        return (List(8) { vowels.random() } + List(8) { consonants.random() }).shuffled()
    }

//    private fun generateRandomLetters(): List<Char> {
//        // Step 1: Get the word list from WordValidator
//        val words = WordValidator.getWordList().filter { it.length <= 16 }
//
//        // Step 2: Randomly pick words from the list
//        val pickedWords = mutableListOf<String>()
//        var totalChars = 0
//        while (totalChars < 16) {
//            val word = words.random()
//            if (totalChars + word.length <= 16) {
//                pickedWords.add(word)
//                totalChars += word.length
//            }
//        }
//
//        // Step 3: For each picked word, convert it into a list of characters
//        val letters = pickedWords.flatMap { it.toList() }
//
//        // Step 4: Shuffle the list of characters
//        val shuffledLetters = letters.shuffled()
//
//        // Step 5: Arrange the characters in a grid (4x4 grid in this case)
//        return shuffledLetters.take(16)
//    }

    private fun onLetterSelected(button: Button) {
        // Append the letter to the selectedLetters
        selectedLetters.append(button.text.toString())
        selectedLettersTextView.text = selectedLetters.toString()

        // Change the button's appearance
        button.apply {
            isEnabled = false // Disable the button if re-selection isn't allowed
        }
    }

    // Get the selected word
    fun getSelectedWord(): String {
        return selectedLetters.toString()
    }

    fun clearSelections() {
        gridLayout.children.forEach { it.isEnabled = true }
        selectedLetters.clear()
        selectedLettersTextView.text = ""
    }
}
