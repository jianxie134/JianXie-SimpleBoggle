package com.example.simpleboggle

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class GameControlsFragment : Fragment() {
    private var actionsListener: GameActionsListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_game_controls, container, false)
        val gameBoardFragment = fragmentManager?.findFragmentById(R.id.gameBoardContainer) as GameBoardFragment
        val word = gameBoardFragment.getSelectedWord()
        view.findViewById<Button>(R.id.submitButton).setOnClickListener { actionsListener?.onSubmitWord(word) }
        view.findViewById<Button>(R.id.clearButton).setOnClickListener { actionsListener?.onClearSelection() }
        view.findViewById<Button>(R.id.newGameButton).setOnClickListener { actionsListener?.onNewGame() }
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

//    private fun submitWord() {
//        // Logic to collect the selected word and submit it
//        actionsListener?.onSubmitWord(collectedWord)
//    }
//
//    private fun clearSelection() {
//        // Logic to clear the current selection
//        actionsListener?.onClearSelection()
//    }
//
//    private fun newGame() {
//        // Logic to initialize a new game
//        actionsListener?.onNewGame()
//    }
}