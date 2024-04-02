package com.example.simpleboggle

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
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

class MainActivity : AppCompatActivity(), GameActionsListener, SensorEventListener {
    private var currentScore = 0
    private val submittedWords = mutableSetOf<String>()

    private lateinit var sensorManager: SensorManager
    private val shakeThreshold = 8000
    private var lastUpdate: Long = 0
    private var last_x = 0f
    private var last_y = 0f
    private var last_z = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)

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

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val currentTime = System.currentTimeMillis()
            if ((currentTime - lastUpdate) > 10) {
                val diffTime = currentTime - lastUpdate
                lastUpdate = currentTime

                val speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000

                if (speed > shakeThreshold) {
                    onNewGame()
                }

                last_x = x
                last_y = y
                last_z = z
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Don't need to do anything here.
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)
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
        // Reset the button states in GameBoardFragment
        resetButtonStates()
    }

    // Add this function to MainActivity
    fun resetButtonStates() {
        val gameBoardFragment = supportFragmentManager.findFragmentById(R.id.gameBoardContainer) as GameBoardFragment
        gameBoardFragment.resetButtonStates()
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
