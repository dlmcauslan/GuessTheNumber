package com.example.dlmcauslan.guessthenumber

import kotlin.random.Random

data class GameState(
        val currentGuess: Int = 5,
        val answer: Int = Random.nextInt(1,11),
        val guessesRemaining: Int = 3,
        val isGuessCorrect: Boolean = false,
        val isGameOver: Boolean = false
) {
    fun increase() = copy(currentGuess = currentGuess + 1)

    fun decrease() = copy(currentGuess = currentGuess - 1)
}

