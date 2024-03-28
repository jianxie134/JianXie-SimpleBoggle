package com.example.simpleboggle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

class MainActivity : AppCompatActivity(), GameActionsListener {
    private var currentScore = 0
    private val submittedWords = mutableSetOf<String>() // Add this line

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lifecycleScope.launch {
            WordValidator.loadWords()
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.gameBoardContainer, GameBoardFragment(), "GAME_BOARD_FRAGMENT")
                .add(R.id.gameControlsContainer, GameControlsFragment(), "GAME_CONTROLS_FRAGMENT")
                .commit()
        }
    }

    override fun onSubmitWord(word: String) {
        if (submittedWords.contains(word)) {
            showToast("This word has already been submitted!")
            onClearSelection()
            return
        }

        if (WordValidator.isValidWord(word)) {
            val scoreDelta = calculateScore(word)
            currentScore += scoreDelta
            showToast("That's correct! +$scoreDelta")
            submittedWords.add(word)
        } else {
            currentScore -= 10
            if (currentScore < 0) {
                currentScore = 0
            }
            showToast("That's incorrect. Try again! -10")
        }
        updateScoreDisplay()
        // Clear the selection in GameBoardFragment
        onClearSelection()
    }

    // Clear the selection in GameBoardFragment
    override fun onClearSelection() {
        val gameBoardFragment = supportFragmentManager.findFragmentById(R.id.gameBoardContainer) as GameBoardFragment
        gameBoardFragment.clearSelections()
    }

    override fun onNewGame() {
        val gameBoardFragment = supportFragmentManager.findFragmentById(R.id.gameBoardContainer) as GameBoardFragment
        gameBoardFragment.clearSelections()
        gameBoardFragment.initializeBoard() // Assuming you have this method to generate a new game board
        currentScore = 0
        updateScoreDisplay()
    }

    private fun calculateScore(word: String): Int {
        var score = 0
        var hasSpecialConsonant = false
        for (char in word.uppercase()) {
            when (char) {
                'A', 'E', 'I', 'O', 'U' -> score += 5
                'S', 'Z', 'P', 'X', 'Q' -> {
                    score += 1
                    hasSpecialConsonant = true
                }
                else -> score += 1
            }
        }
        if (hasSpecialConsonant) {
            score *= 2
        }
        return score
    }

    private fun updateScoreDisplay() {
        // Find the score TextView in GameControlsFragment and update it
        val gameControlsFragment = supportFragmentManager.findFragmentById(R.id.gameControlsContainer) as GameControlsFragment
        val scoreTextView = gameControlsFragment.view?.findViewById<TextView>(R.id.scoreTextView)
        scoreTextView?.text = "Score: $currentScore"
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
