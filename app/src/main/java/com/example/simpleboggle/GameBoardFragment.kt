package com.example.simpleboggle

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.Fragment

class GameBoardFragment : Fragment() {
    private lateinit var gridLayout: GridLayout
    private var selectedLetters = StringBuilder()
    private var actionsListener: GameActionsListener? = null
    private lateinit var selectedLettersTextView: TextView
    // Key: Button, Value: Pair(row, column)
    private val buttonPositions = mutableMapOf<Button, Pair<Int, Int>>()
    // Track the last selected position
    private var lastSelectedPosition: Pair<Int, Int>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_game_board, container, false)
        gridLayout = view.findViewById(R.id.gameBoardGrid)
        initializeBoard()
//        WordValidator.isLoaded.observe(viewLifecycleOwner) { isLoaded ->
//            if (isLoaded) {
//                initializeBoard()  // Now it's safe to call
//            }
//        }
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
        val gridRows = 4
        val gridCols = 4
        for (row in 0 until gridRows) {
            for (col in 0 until gridCols) {
                val button = Button(context).apply {
                    text = letters[row * gridCols + col].toString()
                        setOnClickListener { onLetterSelected(this) }
                }
                // Store the button's position
                buttonPositions[button] = Pair(row, col)
                // Add the button to the grid layout
                gridLayout.addView(button)
            }
        }

        // Add the selectedLettersTextView TextView back to the gridLayout
        selectedLettersTextView = TextView(context).apply {
            id = R.id.selectedLettersTextView
            textSize = 18f
            text = selectedLetters.toString()
        }
        gridLayout.addView(selectedLettersTextView)
    }

    // Generate random letters with 8 vowels and 8 consonants
    private fun generateRandomLetters(): List<Char> {
        // This is a simplified version. Customize this method based on your game's rules.
        val vowels = "AEIOU".toList()
        val consonants = ('A'..'Z').filter { !vowels.contains(it) }
        return (List(8) { vowels.random() } + List(8) { consonants.random() }).shuffled()
    }

    // Generate random letters based words from the wordList
    // App keeps crashing because of accessing the wordList before it's loaded - Solved by using LiveData but not stable
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
        val currentPosition = buttonPositions[button]
        if (currentPosition == null) {
            Toast.makeText(context, "Error in button positioning", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if it's the first selection or if it's adjacent to the last selected letter
        if (lastSelectedPosition == null || isAdjacent(lastSelectedPosition!!, currentPosition)) {
            selectedLetters.append(button.text.toString())
            selectedLettersTextView.text = selectedLetters.toString()

            // Update the appearance of the selected button
            button.isEnabled = false

            // Update the last selected position
            lastSelectedPosition = currentPosition
        } else {
            Toast.makeText(context, "The letter must connect to the previous one!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isAdjacent(lastPos: Pair<Int, Int>, currentPos: Pair<Int, Int>): Boolean {
        val (lastRow, lastCol) = lastPos
        val (currentRow, currentCol) = currentPos
        return Math.abs(lastRow - currentRow) <= 1 && Math.abs(lastCol - currentCol) <= 1
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
