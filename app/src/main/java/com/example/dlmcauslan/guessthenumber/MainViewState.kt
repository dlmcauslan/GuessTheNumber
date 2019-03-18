package com.example.dlmcauslan.guessthenumber

data class MainViewState(
        val guess: Int,
        val answer: Int,
        val guessesRemaining: Int,
        val isGuessCorrect: Boolean,
        val isGameOver: Boolean
)