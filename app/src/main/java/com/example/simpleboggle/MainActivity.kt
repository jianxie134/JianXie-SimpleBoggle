package com.example.simpleboggle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

class MainActivity : AppCompatActivity(), GameActionsListener {
    private var currentScore = 0
    private val wordList: Set<String> by lazy { loadWordList() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.gameBoardContainer, GameBoardFragment())
                .add(R.id.gameControlsContainer, GameControlsFragment())
                .commit()
        }
    }

    private fun loadWordList(): Set<String> {
        // Implementation to load words from assets or resources
        return emptySet()
    }

    override fun onSubmitWord(word: String) {
        if (WordValidator.isValidWord(word, wordList)) {
            val scoreDelta = calculateScore(word)
            currentScore += scoreDelta
            showToast("Correct! +$scoreDelta")
        } else {
            currentScore -= 10
            showToast("Incorrect! -10")
        }
        updateScoreDisplay()
        // Clear the selection in GameBoardFragment
    }

    override fun onClearSelection() {

    }

    override fun onNewGame() {

    }

    private fun calculateScore(word: String): Int {
        // Implementation based on the assignment's scoring rules
        return 0
    }

    private fun updateScoreDisplay() {
        // Find the score TextView in GameControlsFragment and update it
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Implement other GameActionsListener methods...
}
