package com.example.simpleboggle

interface GameActionsListener {
    fun onSubmitWord(word: String)
    fun onClearSelection()
    fun onNewGame()
}