package com.example.simpleboggle

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import androidx.core.view.children
import androidx.fragment.app.Fragment

class GameBoardFragment : Fragment() {
    private lateinit var gridLayout: GridLayout
    private var selectedLetters = StringBuilder()
    private var actionsListener: GameActionsListener? = null

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

//    private fun initializeBoard() {
//        val vowels = listOf('A', 'E', 'I', 'O', 'U')
//        val consonants = ('A'..'Z') - vowels
//        val randomLetters = (List(8) { vowels.random() } + List(8) { consonants.random() }).shuffled()
//
//        for (index in randomLetters.indices) {
//            val button = Button(context)
//            button.text = randomLetters[index].toString()
//            button.setOnClickListener { /* Handle letter selection */ }
//            gridLayout.addView(button)
//        }
//    }

    private fun initializeBoard() {
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
    }

    private fun generateRandomLetters(): List<Char> {
        // This is a simplified version. Customize this method based on your game's rules.
        val vowels = "AEIOU".toList()
        val consonants = ('A'..'Z').filter { !vowels.contains(it) }
        return (List(6) { vowels.random() } + List(10) { consonants.random() }).shuffled()
    }

    private fun onLetterSelected(button: Button) {
        button.isEnabled = false // Prevent re-selection
//        button.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.selectedLetter))
        selectedLetters.append(button.text)
        // Notify the activity (or another fragment through the activity) about the selection
        // Optionally, update a TextView with the current selection for player feedback
    }

    // Get the selected word
    fun getSelectedWord(): String {
        return selectedLetters.toString()
    }

    fun clearSelections() {
        gridLayout.children.forEach { it.isEnabled = true }
        selectedLetters.clear()
    }

    private fun someMethodThatSubmitsAWord(word: String) {
        actionsListener?.onSubmitWord(word)
    }

    // Similar methods for onClearSelection and onNewGame
}
