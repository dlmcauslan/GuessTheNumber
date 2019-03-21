package com.example.dlmcauslan.guessthenumber

data class GameState(
        val currentGuess: Int,
        val answer: Int,
        val guessesRemaining: Int,
        val isGuessCorrect: Boolean,
        val isGameOver: Boolean
)